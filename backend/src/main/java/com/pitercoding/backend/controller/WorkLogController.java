package com.pitercoding.backend.controller;

import com.pitercoding.backend.dto.ActivityEntryResponse;
import com.pitercoding.backend.dto.StartActivityRequest;
import com.pitercoding.backend.dto.WorkDayResponse;
import com.pitercoding.backend.mapper.ActivityEntryMapper;
import com.pitercoding.backend.mapper.WorkDayMapper;
import com.pitercoding.backend.model.ActivityEntry;
import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.service.ActivityEntryService;
import com.pitercoding.backend.service.WorkDayService;
import com.pitercoding.backend.service.WorkLogService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/worklog")
public class WorkLogController {

    private final WorkLogService workLogService;
    private final WorkDayService workDayService;
    private final ActivityEntryService activityEntryService;

    public WorkLogController(
            WorkLogService workLogService,
            WorkDayService workDayService,
            ActivityEntryService activityEntryService
    ) {
        this.workLogService = workLogService;
        this.workDayService = workDayService;
        this.activityEntryService = activityEntryService;
    }

    @PostMapping("/{employeeId}/start")
    public ResponseEntity<ActivityEntryResponse> startActivity(
            @PathVariable Long employeeId,
            @Valid @RequestBody StartActivityRequest request
    ) {
        ActivityEntry entry = workLogService.startActivity(
                employeeId,
                request.getProgramId(),
                request.getTeamId(),
                request.getLanguageId(),
                request.getActivityId(),
                request.getSubactivityId(),
                request.getNote()
        );
        return ResponseEntity.ok(ActivityEntryMapper.toResponse(entry));
    }

    @PostMapping("/{employeeId}/save")
    public ResponseEntity<WorkDayResponse> saveDay(@PathVariable Long employeeId) {
        WorkDay workDay = workLogService.saveDay(employeeId);
        List<ActivityEntry> entries = activityEntryService.findByWorkDayId(workDay.getId());
        WorkDayResponse response = WorkDayMapper.toResponse(workDay, null, entries);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<WorkDayResponse> getWorkDay(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        WorkDay workDay = workDayService.findByEmployeeAndDate(employeeId, date)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "No workday for employee: " + employeeId
                ));
        ActivityEntry current = activityEntryService.findOpenByWorkDayId(workDay.getId()).orElse(null);
        List<ActivityEntry> entries = activityEntryService.findByWorkDayId(workDay.getId());
        WorkDayResponse response = WorkDayMapper.toResponse(workDay, current, entries);
        return ResponseEntity.ok(response);
    }
}
