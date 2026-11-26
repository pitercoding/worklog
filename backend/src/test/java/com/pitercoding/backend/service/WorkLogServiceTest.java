package com.pitercoding.backend.service;

import com.pitercoding.backend.model.Activity;
import com.pitercoding.backend.model.ActivityEntry;
import com.pitercoding.backend.model.Employee;
import com.pitercoding.backend.model.Language;
import com.pitercoding.backend.model.Program;
import com.pitercoding.backend.model.Subactivity;
import com.pitercoding.backend.model.Team;
import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.model.WorkDayStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkLogServiceTest {

    private static final Instant NOW = Instant.parse("2026-03-10T10:00:00Z");
    private static final LocalDate TODAY = LocalDate.of(2026, 3, 10);
    private static final long EMPLOYEE_ID = 1L;

    @Mock EmployeeService employeeService;
    @Mock ProgramService programService;
    @Mock TeamService teamService;
    @Mock LanguageService languageService;
    @Mock ActivityService activityService;
    @Mock SubactivityService subactivityService;
    @Mock WorkDayService workDayService;
    @Mock ActivityEntryService activityEntryService;

    private WorkLogService service;

    @BeforeEach
    void setUp() {
        service = newService(Clock.fixed(NOW, ZoneOffset.UTC));

        Activity activity = new Activity(10L, "Activity", true);
        lenient().when(programService.getById(1L)).thenReturn(new Program(1L, "Program", true));
        lenient().when(teamService.getById(2L)).thenReturn(new Team(2L, "Team", true));
        lenient().when(languageService.getById(3L)).thenReturn(new Language(3L, "Java", true));
        lenient().when(activityService.getById(10L)).thenReturn(activity);
        lenient().when(subactivityService.getById(100L)).thenReturn(new Subactivity(100L, activity, "Sub", true));
        lenient().when(workDayService.save(any(WorkDay.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(activityEntryService.save(any(ActivityEntry.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void startActivity_createsWorkDayForTodayWhenNoneExists() {
        when(workDayService.findByEmployeeAndDate(EMPLOYEE_ID, TODAY)).thenReturn(Optional.empty());
        when(employeeService.getById(EMPLOYEE_ID)).thenReturn(new Employee());

        ActivityEntry entry = service.startActivity(EMPLOYEE_ID, 1L, 2L, 3L, 10L, 100L, "note");

        assertThat(entry.getWorkDay().getStatus()).isEqualTo(WorkDayStatus.OPEN);
        assertThat(entry.getWorkDay().getDate()).isEqualTo(TODAY);
        assertThat(entry.getStartedAt().toInstant()).isEqualTo(NOW);
        assertThat(entry.getNote()).isEqualTo("note");
        assertThat(entry.getFinishedAt()).isNull();
    }

    @Test
    void startActivity_usesConfiguredTimeZoneToDecideTheDate() {
        // 10:00 UTC is already the next day in UTC+14
        WorkLogService kiritimati = newService(Clock.fixed(NOW, ZoneId.of("Pacific/Kiritimati")));
        when(workDayService.findByEmployeeAndDate(EMPLOYEE_ID, TODAY.plusDays(1))).thenReturn(Optional.empty());
        when(employeeService.getById(EMPLOYEE_ID)).thenReturn(new Employee());

        ActivityEntry entry = kiritimati.startActivity(EMPLOYEE_ID, 1L, 2L, 3L, 10L, 100L, null);

        assertThat(entry.getWorkDay().getDate()).isEqualTo(TODAY.plusDays(1));
    }

    @Test
    void startActivity_closesTheRunningActivityAndComputesItsDuration() {
        WorkDay workDay = workDay(WorkDayStatus.OPEN);
        ActivityEntry running = new ActivityEntry();
        running.setStartedAt(OffsetDateTime.ofInstant(NOW.minusSeconds(90), ZoneOffset.UTC));
        when(workDayService.findByEmployeeAndDate(EMPLOYEE_ID, TODAY)).thenReturn(Optional.of(workDay));
        when(activityEntryService.findOpenByWorkDayId(workDay.getId())).thenReturn(Optional.of(running));

        service.startActivity(EMPLOYEE_ID, 1L, 2L, 3L, 10L, 100L, null);

        assertThat(running.getFinishedAt().toInstant()).isEqualTo(NOW);
        assertThat(running.getDurationSeconds()).isEqualTo(90L);
    }

    @Test
    void startActivity_isRejectedWhenTheDayIsAlreadyFinished() {
        when(workDayService.findByEmployeeAndDate(EMPLOYEE_ID, TODAY))
                .thenReturn(Optional.of(workDay(WorkDayStatus.FINISHED)));

        assertThatThrownBy(() -> service.startActivity(EMPLOYEE_ID, 1L, 2L, 3L, 10L, 100L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already finished");
        verify(activityEntryService, never()).save(any());
    }

    @Test
    void startActivity_isRejectedWhenTheSubactivityBelongsToAnotherActivity() {
        WorkDay workDay = workDay(WorkDayStatus.OPEN);
        when(workDayService.findByEmployeeAndDate(EMPLOYEE_ID, TODAY)).thenReturn(Optional.of(workDay));
        when(activityEntryService.findOpenByWorkDayId(workDay.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startActivity(EMPLOYEE_ID, 1L, 2L, 3L, 999L, 100L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong");
        verify(activityEntryService, never()).save(any());
    }

    @Test
    void saveDay_closesTheRunningActivityAndFinishesTheDay() {
        WorkDay workDay = workDay(WorkDayStatus.OPEN);
        ActivityEntry running = new ActivityEntry();
        running.setStartedAt(OffsetDateTime.ofInstant(NOW.minusSeconds(30), ZoneOffset.UTC));
        when(workDayService.findOpenByEmployeeAndDate(EMPLOYEE_ID, TODAY)).thenReturn(Optional.of(workDay));
        when(activityEntryService.findOpenByWorkDayId(workDay.getId())).thenReturn(Optional.of(running));

        WorkDay result = service.saveDay(EMPLOYEE_ID);

        assertThat(result.getStatus()).isEqualTo(WorkDayStatus.FINISHED);
        assertThat(result.getFinishedAt().toInstant()).isEqualTo(NOW);
        assertThat(running.getDurationSeconds()).isEqualTo(30L);
    }

    @Test
    void saveDay_isRejectedWhenThereIsNoOpenWorkDay() {
        when(workDayService.findOpenByEmployeeAndDate(EMPLOYEE_ID, TODAY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.saveDay(EMPLOYEE_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No open workday");
    }

    private WorkLogService newService(Clock clock) {
        return new WorkLogService(employeeService, programService, teamService, languageService,
                activityService, subactivityService, workDayService, activityEntryService, clock);
    }

    private static WorkDay workDay(WorkDayStatus status) {
        WorkDay workDay = new WorkDay();
        workDay.setId(50L);
        workDay.setDate(TODAY);
        workDay.setStartedAt(OffsetDateTime.ofInstant(NOW.minusSeconds(3600), ZoneOffset.UTC));
        workDay.setStatus(status);
        return workDay;
    }
}
