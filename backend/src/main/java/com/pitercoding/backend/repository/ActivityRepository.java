package com.pitercoding.backend.repository;

import com.pitercoding.backend.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findAllByActiveTrue();
}
