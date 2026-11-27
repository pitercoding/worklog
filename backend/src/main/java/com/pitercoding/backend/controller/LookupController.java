package com.pitercoding.backend.controller;

import com.pitercoding.backend.dto.LookupResponse;
import com.pitercoding.backend.dto.SubactivityItem;
import com.pitercoding.backend.mapper.LookupMapper;
import com.pitercoding.backend.service.ActivityService;
import com.pitercoding.backend.service.LanguageService;
import com.pitercoding.backend.service.ProgramService;
import com.pitercoding.backend.service.SubactivityService;
import com.pitercoding.backend.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lookups")
@Tag(name = "Lookups", description = "Reference data used by the activity form")
public class LookupController {

    private final ProgramService programService;
    private final TeamService teamService;
    private final LanguageService languageService;
    private final ActivityService activityService;
    private final SubactivityService subactivityService;

    public LookupController(
            ProgramService programService,
            TeamService teamService,
            LanguageService languageService,
            ActivityService activityService,
            SubactivityService subactivityService
    ) {
        this.programService = programService;
        this.teamService = teamService;
        this.languageService = languageService;
        this.activityService = activityService;
        this.subactivityService = subactivityService;
    }

    @Operation(summary = "List all active lookups", description = "Programs, teams, languages, activities and subactivities.")
    @GetMapping
    public ResponseEntity<LookupResponse> getAllLookups() {
        LookupResponse response = new LookupResponse();
        response.setPrograms(programService.getActivePrograms().stream()
                .map(LookupMapper::toItem)
                .collect(Collectors.toList()));
        response.setTeams(teamService.getActiveTeams().stream()
                .map(LookupMapper::toItem)
                .collect(Collectors.toList()));
        response.setLanguages(languageService.getActiveLanguages().stream()
                .map(LookupMapper::toItem)
                .collect(Collectors.toList()));
        response.setActivities(activityService.getActiveActivities().stream()
                .map(LookupMapper::toItem)
                .collect(Collectors.toList()));
        response.setSubactivities(subactivityService.getActiveSubactivities().stream()
                .map(LookupMapper::toItem)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List active subactivities of an activity")
    @GetMapping("/subactivities")
    public ResponseEntity<List<SubactivityItem>> getSubactivities(
            @RequestParam Long activityId
    ) {
        List<SubactivityItem> items = subactivityService.getActiveSubactivitiesByActivity(activityId)
                .stream()
                .map(LookupMapper::toItem)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
}
