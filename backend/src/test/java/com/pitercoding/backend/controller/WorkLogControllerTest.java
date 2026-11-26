package com.pitercoding.backend.controller;

import com.pitercoding.backend.exception.NotFoundException;
import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.service.ActivityEntryService;
import com.pitercoding.backend.service.WorkDayService;
import com.pitercoding.backend.service.WorkLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkLogController.class)
@Import(WorkLogControllerTest.FixedClockConfig.class)
class WorkLogControllerTest {

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-03-10T10:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired MockMvc mockMvc;
    @MockitoBean WorkLogService workLogService;
    @MockitoBean WorkDayService workDayService;
    @MockitoBean ActivityEntryService activityEntryService;

    @Test
    void start_returns400WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/worklog/1/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"programId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/worklog/1/start"));
    }

    @Test
    void start_returns409WhenTheDayIsAlreadyFinished() throws Exception {
        when(workLogService.startActivity(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("Workday is already finished for employee: 1"));

        mockMvc.perform(post("/api/worklog/1/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validStartBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Workday is already finished for employee: 1"));
    }

    @Test
    void start_returns400WhenTheSubactivityDoesNotMatchTheActivity() throws Exception {
        when(workLogService.startActivity(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Subactivity does not belong to the selected activity"));

        mockMvc.perform(post("/api/worklog/1/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validStartBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Subactivity does not belong to the selected activity"));
    }

    @Test
    void getWorkDay_returns404WhenThereIsNoWorkDay() throws Exception {
        when(workDayService.findByEmployeeAndDate(1L, LocalDate.of(2026, 3, 9))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/worklog/1").param("date", "2026-03-09"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getWorkDay_defaultsToTodayInTheApplicationTimeZone() throws Exception {
        WorkDay workDay = new WorkDay();
        workDay.setId(7L);
        workDay.setDate(LocalDate.of(2026, 3, 10));
        workDay.setStartedAt(OffsetDateTime.parse("2026-03-10T08:00:00Z"));
        when(workDayService.findByEmployeeAndDate(1L, LocalDate.of(2026, 3, 10))).thenReturn(Optional.of(workDay));
        when(activityEntryService.findOpenByWorkDayId(7L)).thenReturn(Optional.empty());
        when(activityEntryService.findByWorkDayId(7L)).thenReturn(List.of());

        mockMvc.perform(get("/api/worklog/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-03-10"))
                .andExpect(jsonPath("$.entries").isEmpty());
        verify(workDayService).findByEmployeeAndDate(1L, LocalDate.of(2026, 3, 10));
    }

    @Test
    void getWorkDay_returns400ForAnInvalidDate() throws Exception {
        mockMvc.perform(get("/api/worklog/1").param("date", "not-a-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void start_returns404WhenTheEmployeeDoesNotExist() throws Exception {
        when(workLogService.startActivity(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new NotFoundException("Employee not found: 99"));

        mockMvc.perform(post("/api/worklog/99/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validStartBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found: 99"));
    }

    @Test
    void start_returns400WhenTheNoteIsTooLong() throws Exception {
        String body = "{\"programId\":1,\"teamId\":2,\"languageId\":3,\"activityId\":10,\"subactivityId\":100,\"note\":\""
                + "x".repeat(256) + "\"}";

        mockMvc.perform(post("/api/worklog/1/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith("note ")));
    }

    @Test
    void start_returns400ForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/worklog/1/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/worklog/1/start"));
    }

    @Test
    void start_returns500WithoutLeakingDetailsForUnexpectedErrors() throws Exception {
        when(workLogService.startActivity(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("secret internal detail"));

        mockMvc.perform(post("/api/worklog/1/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validStartBody()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }

    @Test
    void unknownRoute_returns404InTheStandardFormat() throws Exception {
        mockMvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/does-not-exist"));
    }

    @Test
    void wrongHttpMethod_returns405InTheStandardFormat() throws Exception {
        mockMvc.perform(get("/api/worklog/1/start"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    private static String validStartBody() {
        return "{\"programId\":1,\"teamId\":2,\"languageId\":3,\"activityId\":10,\"subactivityId\":100}";
    }
}
