package com.pitercoding.backend.integration;

import com.pitercoding.backend.model.Employee;
import com.pitercoding.backend.model.Subactivity;
import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.model.WorkDayStatus;
import com.pitercoding.backend.repository.ActivityEntryRepository;
import com.pitercoding.backend.repository.EmployeeRepository;
import com.pitercoding.backend.repository.LanguageRepository;
import com.pitercoding.backend.repository.ProgramRepository;
import com.pitercoding.backend.repository.SubactivityRepository;
import com.pitercoding.backend.repository.TeamRepository;
import com.pitercoding.backend.repository.WorkDayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runs the real application against a real PostgreSQL (Testcontainers).
 * Requires Docker; skipped automatically when Docker is not available.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class WorkLogFlowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired MockMvc mockMvc;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired ProgramRepository programRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired LanguageRepository languageRepository;
    @Autowired SubactivityRepository subactivityRepository;
    @Autowired WorkDayRepository workDayRepository;
    @Autowired ActivityEntryRepository activityEntryRepository;

    private long employeeId;
    private long programId;
    private long teamId;
    private long languageId;
    private long activityId;
    private long subactivityId;

    @BeforeEach
    void setUp() {
        activityEntryRepository.deleteAll();
        workDayRepository.deleteAll();

        employeeId = employeeRepository.findAllByActiveTrue().get(0).getId();
        programId = programRepository.findAll().get(0).getId();
        teamId = teamRepository.findAll().get(0).getId();
        languageId = languageRepository.findAll().get(0).getId();
        Subactivity subactivity = subactivityRepository.findAll().get(0);
        subactivityId = subactivity.getId();
        activityId = subactivity.getActivity().getId();
    }

    @Test
    void fullDayFlow_startSwitchSaveAndLock() throws Exception {
        start(activityId).andExpect(status().isOk());
        start(activityId).andExpect(status().isOk());

        mockMvc.perform(get("/api/worklog/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.entries.length()").value(2))
                .andExpect(jsonPath("$.entries[0].finishedAt").isNotEmpty())
                .andExpect(jsonPath("$.currentEntry.finishedAt").isEmpty());

        mockMvc.perform(post("/api/worklog/{id}/save", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.entries[1].durationSeconds").isNumber());

        start(activityId).andExpect(status().isConflict());
    }

    @Test
    void startActivity_rejectsASubactivityFromAnotherActivity() throws Exception {
        start(activityId + 1_000_000).andExpect(status().isBadRequest());
    }

    @Test
    void startActivity_returns404ForAnUnknownEmployee() throws Exception {
        employeeId = 999_999;

        start(activityId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found: 999999"));
    }

    @Test
    void startActivity_returns404ForAnUnknownSubactivity() throws Exception {
        subactivityId = 999_999;

        start(activityId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Subactivity not found: 999999"));
    }

    @Test
    void getWorkDay_returns404WhenNothingWasStarted() throws Exception {
        mockMvc.perform(get("/api/worklog/{id}", employeeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void database_allowsOnlyOneWorkDayPerEmployeePerDate() {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        workDayRepository.saveAndFlush(newWorkDay(employee));

        assertThatThrownBy(() -> workDayRepository.saveAndFlush(newWorkDay(employee)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(workDayRepository.count()).isEqualTo(1);
    }

    private ResultActions start(long activity) throws Exception {
        String body = "{\"programId\":%d,\"teamId\":%d,\"languageId\":%d,\"activityId\":%d,\"subactivityId\":%d}"
                .formatted(programId, teamId, languageId, activity, subactivityId);
        return mockMvc.perform(post("/api/worklog/{id}/start", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static WorkDay newWorkDay(Employee employee) {
        WorkDay workDay = new WorkDay();
        workDay.setEmployee(employee);
        workDay.setDate(LocalDate.of(2026, 1, 15));
        workDay.setStartedAt(OffsetDateTime.parse("2026-01-15T09:00:00Z"));
        workDay.setStatus(WorkDayStatus.OPEN);
        return workDay;
    }
}
