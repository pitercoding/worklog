package com.pitercoding.backend.service;

import com.pitercoding.backend.model.ActivityEntry;
import com.pitercoding.backend.model.Subactivity;
import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.model.WorkDayStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
public class WorkLogService {
    private final EmployeeService employeeService;
    private final ProgramService programService;
    private final TeamService teamService;
    private final LanguageService languageService;
    private final ActivityService activityService;
    private final SubactivityService subactivityService;
    private final WorkDayService workDayService;
    private final ActivityEntryService activityEntryService;

    public WorkLogService(
            EmployeeService employeeService,
            ProgramService programService,
            TeamService teamService,
            LanguageService languageService,
            ActivityService activityService,
            SubactivityService subactivityService,
            WorkDayService workDayService,
            ActivityEntryService activityEntryService
    ) {
        this.employeeService = employeeService;
        this.programService = programService;
        this.teamService = teamService;
        this.languageService = languageService;
        this.activityService = activityService;
        this.subactivityService = subactivityService;
        this.workDayService = workDayService;
        this.activityEntryService = activityEntryService;
    }

    @Transactional
    public ActivityEntry startActivity(
            Long employeeId,
            Long programId,
            Long teamId,
            Long languageId,
            Long activityId,
            Long subactivityId,
            String note
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        LocalDate today = now.toLocalDate();

        WorkDay workDay = workDayService.findByEmployeeAndDate(employeeId, today)
                .map(existing -> {
                    if (existing.getStatus() == WorkDayStatus.FINISHED) {
                        throw new IllegalStateException("Workday is already finished for employee: " + employeeId);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    WorkDay created = new WorkDay();
                    created.setEmployee(employeeService.getById(employeeId));
                    created.setDate(today);
                    created.setStartedAt(now);
                    created.setStatus(WorkDayStatus.OPEN);
                    return workDayService.save(created);
                });

        activityEntryService.findOpenByWorkDayId(workDay.getId())
                .ifPresent(open -> closeEntry(open, now));

        Subactivity subactivity = subactivityService.getById(subactivityId);
        if (subactivity.getActivity() == null || subactivity.getActivity().getId() == null
                || !subactivity.getActivity().getId().equals(activityId)) {
            throw new IllegalArgumentException("Subactivity does not belong to the selected activity");
        }

        ActivityEntry entry = new ActivityEntry();
        entry.setWorkDay(workDay);
        entry.setProgram(programService.getById(programId));
        entry.setTeam(teamService.getById(teamId));
        entry.setLanguage(languageService.getById(languageId));
        entry.setActivity(activityService.getById(activityId));
        entry.setSubactivity(subactivity);
        entry.setStartedAt(now);
        entry.setNote(note);

        return activityEntryService.save(entry);
    }

    @Transactional
    public WorkDay saveDay(Long employeeId) {
        OffsetDateTime now = OffsetDateTime.now();
        LocalDate today = now.toLocalDate();

        WorkDay workDay = workDayService.findOpenByEmployeeAndDate(employeeId, today)
                .orElseThrow(() -> new IllegalStateException("No open workday for employee: " + employeeId));

        activityEntryService.findOpenByWorkDayId(workDay.getId())
                .ifPresent(open -> closeEntry(open, now));

        workDay.setFinishedAt(now);
        workDay.setStatus(WorkDayStatus.FINISHED);
        return workDayService.save(workDay);
    }

    private void closeEntry(ActivityEntry entry, OffsetDateTime finishedAt) {
        entry.setFinishedAt(finishedAt);
        long seconds = Duration.between(entry.getStartedAt(), finishedAt).getSeconds();
        entry.setDurationSeconds(Math.max(0, seconds));
        activityEntryService.save(entry);
    }
}
