package com.pitercoding.backend.repository;

import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.model.WorkDayStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkDayRepository extends JpaRepository<WorkDay, Long> {
    Optional<WorkDay> findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    Optional<WorkDay> findByEmployeeIdAndDateAndStatus(Long employeeId, LocalDate date, WorkDayStatus status);
}
