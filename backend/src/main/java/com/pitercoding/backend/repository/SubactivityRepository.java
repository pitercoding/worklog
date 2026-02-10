package com.pitercoding.backend.repository;

import com.pitercoding.backend.model.Subactivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubactivityRepository extends JpaRepository<Subactivity, Long> {
    List<Subactivity> findAllByActivityIdAndActiveTrue(Long activityId);
}
