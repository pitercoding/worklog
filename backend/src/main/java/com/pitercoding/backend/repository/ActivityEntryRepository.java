package com.pitercoding.backend.repository;

import com.pitercoding.backend.model.ActivityEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityEntryRepository extends JpaRepository<ActivityEntry, Long> {
    Optional<ActivityEntry> findFirstByWorkDayIdAndFinishedAtIsNull(Long workDayId);
    List<ActivityEntry> findAllByWorkDayIdOrderByStartedAtAsc(Long workDayId);
}
