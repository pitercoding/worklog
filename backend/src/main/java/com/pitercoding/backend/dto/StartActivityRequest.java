package com.pitercoding.backend.dto;

import jakarta.validation.constraints.NotNull;

public class StartActivityRequest {
    @NotNull
    private Long programId;
    @NotNull
    private Long teamId;
    @NotNull
    private Long languageId;
    @NotNull
    private Long activityId;
    @NotNull
    private Long subactivityId;
    private String note;

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getSubactivityId() {
        return subactivityId;
    }

    public void setSubactivityId(Long subactivityId) {
        this.subactivityId = subactivityId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
