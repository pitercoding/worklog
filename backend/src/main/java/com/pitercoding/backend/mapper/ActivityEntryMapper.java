package com.pitercoding.backend.mapper;

import com.pitercoding.backend.dto.ActivityEntryResponse;
import com.pitercoding.backend.model.ActivityEntry;

public final class ActivityEntryMapper {
    private ActivityEntryMapper() {
    }

    /**
     * ActivityEntryMapper: converte ActivityEntry → ActivityEntryResponse
     */
    public static ActivityEntryResponse toResponse(ActivityEntry entry) {
        if (entry == null) {
            return null;
        }
        ActivityEntryResponse response = new ActivityEntryResponse();
        response.setId(entry.getId());
        response.setProgram(LookupMapper.toItem(entry.getProgram()));
        response.setTeam(LookupMapper.toItem(entry.getTeam()));
        response.setLanguage(LookupMapper.toItem(entry.getLanguage()));
        response.setActivity(LookupMapper.toItem(entry.getActivity()));
        response.setSubactivity(LookupMapper.toItem(entry.getSubactivity()));
        response.setStartedAt(entry.getStartedAt());
        response.setFinishedAt(entry.getFinishedAt());
        response.setDurationSeconds(entry.getDurationSeconds());
        response.setNote(entry.getNote());
        return response;
    }
}
