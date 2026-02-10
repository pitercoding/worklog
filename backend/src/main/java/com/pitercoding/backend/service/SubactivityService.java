package com.pitercoding.backend.service;

import com.pitercoding.backend.model.Subactivity;
import com.pitercoding.backend.repository.SubactivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubactivityService {
    private final SubactivityRepository subactivityRepository;

    public SubactivityService(SubactivityRepository subactivityRepository) {
        this.subactivityRepository = subactivityRepository;
    }

    public List<Subactivity> getActiveSubactivities() {
        return subactivityRepository.findAllByActiveTrue();
    }

    public List<Subactivity> getActiveSubactivitiesByActivity(Long activityId) {
        return subactivityRepository.findAllByActivityIdAndActiveTrue(activityId);
    }

    public Subactivity getById(Long id) {
        return subactivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subactivity not found: " + id));
    }
}
