package com.pitercoding.backend.service;

import com.pitercoding.backend.model.WorkDay;
import com.pitercoding.backend.model.WorkDayStatus;
import com.pitercoding.backend.repository.WorkDayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WorkDayService {
    private final WorkDayRepository workDayRepository;

    public WorkDayService(WorkDayRepository workDayRepository) {
        this.workDayRepository = workDayRepository;
    }

    public Optional<WorkDay> findByEmployeeAndDate(Long employeeId, LocalDate date) {
        return workDayRepository.findByEmployeeIdAndDate(employeeId, date);
    }

    public Optional<WorkDay> findOpenByEmployeeAndDate(Long employeeId, LocalDate date) {
        return workDayRepository.findByEmployeeIdAndDateAndStatus(employeeId, date, WorkDayStatus.OPEN);
    }

    public WorkDay save(WorkDay workDay) {
        return workDayRepository.save(workDay);
    }
}
