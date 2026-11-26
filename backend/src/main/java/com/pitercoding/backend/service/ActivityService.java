package com.pitercoding.backend.service;

import com.pitercoding.backend.exception.NotFoundException;
import com.pitercoding.backend.model.Activity;
import com.pitercoding.backend.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getActiveActivities() {
        return activityRepository.findAllByActiveTrue();
    }

    public Activity getById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found: " + id));
    }
}
