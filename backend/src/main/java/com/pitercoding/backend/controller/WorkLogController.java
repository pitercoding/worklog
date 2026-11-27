package com.pitercoding.backend.controller;

import com.pitercoding.backend.dto.ActivityEntryResponse;
import com.pitercoding.backend.dto.ErrorResponse;
import com.pitercoding.backend.dto.StartActivityRequest;
import com.pitercoding.backend.dto.WorkDayResponse;
import com.pitercoding.backend.exception.NotFoundException;
import com.pitercoding.backend.mapper.ActivityEntryMapper;
import com.pitercoding.backend.mapper.WorkDayMapper;
import com.pitercoding.backend.model.ActivityEntry;
import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.service.ActivityEntryService;
import com.pitercoding.backend.service.WorkDayService;
import com.pitercoding.backend.service.WorkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/worklog")
@Tag(name = "Worklog", description = "Start activities, finish the day and read a workday")
public class WorkLogController {

    private final WorkLogService workLogService;
    private final WorkDayService workDayService;
    private final ActivityEntryService activityEntryService;
    private final Clock clock;

    public WorkLogController(
            WorkLogService workLogService,
            WorkDayService workDayService,
            ActivityEntryService activityEntryService,
            Clock clock
    ) {
        this.workLogService = workLogService;
        this.workDayService = workDayService;
        this.activityEntryService = activityEntryService;
        this.clock = clock;
    }

    @Operation(
            summary = "Start an activity",
            description = "Opens a new activity for today and closes the one that is running. "
                    + "Creates today's workday if it does not exist yet."
    )
    @ApiResponse(responseCode = "200", description = "Activity started")
    @ApiResponse(responseCode = "400", description = "Invalid request or subactivity does not belong to the activity", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Employee, program, team, language, activity or subactivity not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "The workday is already finished", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

    @Operation(
            summary = "Save (finish) the day",
            description = "Closes the running activity and finishes today's workday. No more activities can be started today."
    )
    @ApiResponse(responseCode = "200", description = "Workday finished")
    @ApiResponse(responseCode = "409", description = "There is no open workday for today", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{employeeId}/save")
    public ResponseEntity<WorkDayResponse> saveDay(@PathVariable Long employeeId) {
        WorkDay workDay = workLogService.saveDay(employeeId);
        List<ActivityEntry> entries = activityEntryService.findByWorkDayId(workDay.getId());
        WorkDayResponse response = WorkDayMapper.toResponse(workDay, null, entries);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a workday",
            description = "Returns the workday with its activities. Without the date parameter it returns today, "
                    + "according to the application time zone."
    )
    @ApiResponse(responseCode = "200", description = "Workday found")
    @ApiResponse(responseCode = "400", description = "Invalid date (use YYYY-MM-DD)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No workday for this employee and date", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{employeeId}")
    public ResponseEntity<WorkDayResponse> getWorkDay(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate day = date != null ? date : LocalDate.now(clock);
        WorkDay workDay = workDayService.findByEmployeeAndDate(employeeId, day)
                .orElseThrow(() -> new NotFoundException("No workday for employee: " + employeeId));
        ActivityEntry current = activityEntryService.findOpenByWorkDayId(workDay.getId()).orElse(null);
        List<ActivityEntry> entries = activityEntryService.findByWorkDayId(workDay.getId());
        WorkDayResponse response = WorkDayMapper.toResponse(workDay, current, entries);
        return ResponseEntity.ok(response);
    }
}
