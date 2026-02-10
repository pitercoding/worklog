package com.pitercoding.backend.dto;

import com.pitercoding.backend.model.WorkDayStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class WorkDayResponse {
    private Long id;
    private LocalDate date;
    private OffsetDateTime startedAt;
    private OffsetDateTime finishedAt;
    private WorkDayStatus status;
    private ActivityEntryResponse currentEntry;
    private List<ActivityEntryResponse> entries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public WorkDayStatus getStatus() {
        return status;
    }

    public void setStatus(WorkDayStatus status) {
        this.status = status;
    }

    public ActivityEntryResponse getCurrentEntry() {
        return currentEntry;
    }

    public void setCurrentEntry(ActivityEntryResponse currentEntry) {
        this.currentEntry = currentEntry;
    }

    public List<ActivityEntryResponse> getEntries() {
        return entries;
    }

    public void setEntries(List<ActivityEntryResponse> entries) {
        this.entries = entries;
    }
}
