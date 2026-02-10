package com.pitercoding.backend.mapper;

import com.pitercoding.backend.dto.ActivityEntryResponse;
import com.pitercoding.backend.dto.WorkDayResponse;
import com.pitercoding.backend.model.ActivityEntry;
import com.pitercoding.backend.model.WorkDay;

import java.util.List;
import java.util.stream.Collectors;

public final class WorkDayMapper {
    private WorkDayMapper() {
    }

    /**
     * WorkDayMapper: converte WorkDay + entries → WorkDayResponse
     */

    public static WorkDayResponse toResponse(WorkDay workDay, ActivityEntry currentEntry, List<ActivityEntry> entries) {
        if (workDay == null) {
            return null;
        }
        WorkDayResponse response = new WorkDayResponse();
        response.setId(workDay.getId());
        response.setDate(workDay.getDate());
        response.setStartedAt(workDay.getStartedAt());
        response.setFinishedAt(workDay.getFinishedAt());
        response.setStatus(workDay.getStatus());
        response.setCurrentEntry(ActivityEntryMapper.toResponse(currentEntry));
        response.setEntries(toEntryResponses(entries));
        return response;
    }

    private static List<ActivityEntryResponse> toEntryResponses(List<ActivityEntry> entries) {
        if (entries == null) {
            return null;
        }
        return entries.stream()
                .map(ActivityEntryMapper::toResponse)
                .collect(Collectors.toList());
    }
}
