package com.pitercoding.backend.mapper;

import com.pitercoding.backend.dto.LookupItem;
import com.pitercoding.backend.dto.SubactivityItem;
import com.pitercoding.backend.model.Activity;
import com.pitercoding.backend.model.Language;
import com.pitercoding.backend.model.Program;
import com.pitercoding.backend.model.Subactivity;
import com.pitercoding.backend.model.Team;

public final class LookupMapper {
    private LookupMapper() {
    }

    /**
     * LookupMapper: converte entidades de lookup → LookupItem / SubactivityItem
     */

    public static LookupItem toItem(Program program) {
        if (program == null) {
            return null;
        }
        LookupItem item = new LookupItem();
        item.setId(program.getId());
        item.setName(program.getName());
        return item;
    }

    public static LookupItem toItem(Team team) {
        if (team == null) {
            return null;
        }
        LookupItem item = new LookupItem();
        item.setId(team.getId());
        item.setName(team.getName());
        return item;
    }

    public static LookupItem toItem(Language language) {
        if (language == null) {
            return null;
        }
        LookupItem item = new LookupItem();
        item.setId(language.getId());
        item.setName(language.getName());
        return item;
    }

    public static LookupItem toItem(Activity activity) {
        if (activity == null) {
            return null;
        }
        LookupItem item = new LookupItem();
        item.setId(activity.getId());
        item.setName(activity.getName());
        return item;
    }

    public static SubactivityItem toItem(Subactivity subactivity) {
        if (subactivity == null) {
            return null;
        }
        SubactivityItem item = new SubactivityItem();
        item.setId(subactivity.getId());
        item.setName(subactivity.getName());
        if (subactivity.getActivity() != null) {
            item.setActivityId(subactivity.getActivity().getId());
        }
        return item;
    }
}
