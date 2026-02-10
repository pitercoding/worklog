package com.pitercoding.backend.dto;

import java.time.OffsetDateTime;

public class ActivityEntryResponse {
    private Long id;
    private LookupItem program;
    private LookupItem team;
    private LookupItem language;
    private LookupItem activity;
    private SubactivityItem subactivity;
    private OffsetDateTime startedAt;
    private OffsetDateTime finishedAt;
    private Long durationSeconds;
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LookupItem getProgram() {
        return program;
    }

    public void setProgram(LookupItem program) {
        this.program = program;
    }

    public LookupItem getTeam() {
        return team;
    }

    public void setTeam(LookupItem team) {
        this.team = team;
    }

    public LookupItem getLanguage() {
        return language;
    }

    public void setLanguage(LookupItem language) {
        this.language = language;
    }

    public LookupItem getActivity() {
        return activity;
    }

    public void setActivity(LookupItem activity) {
        this.activity = activity;
    }

    public SubactivityItem getSubactivity() {
        return subactivity;
    }

    public void setSubactivity(SubactivityItem subactivity) {
        this.subactivity = subactivity;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(OffsetDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
