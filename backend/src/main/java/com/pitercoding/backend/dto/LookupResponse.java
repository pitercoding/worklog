package com.pitercoding.backend.dto;

import java.util.List;

public class LookupResponse {
    private List<LookupItem> programs;
    private List<LookupItem> teams;
    private List<LookupItem> languages;
    private List<LookupItem> activities;
    private List<SubactivityItem> subactivities;

    public List<LookupItem> getPrograms() {
        return programs;
    }

    public void setPrograms(List<LookupItem> programs) {
        this.programs = programs;
    }

    public List<LookupItem> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LookupItem> languages) {
        this.languages = languages;
    }

    public List<LookupItem> getTeams() {
        return teams;
    }

    public void setTeams(List<LookupItem> teams) {
        this.teams = teams;
    }

    public List<LookupItem> getActivities() {
        return activities;
    }

    public void setActivities(List<LookupItem> activities) {
        this.activities = activities;
    }

    public List<SubactivityItem> getSubactivities() {
        return subactivities;
    }

    public void setSubactivities(List<SubactivityItem> subactivities) {
        this.subactivities = subactivities;
    }
}
