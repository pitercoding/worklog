package com.pitercoding.backend.service;

import com.pitercoding.backend.model.ActivityEntry;
import com.pitercoding.backend.repository.ActivityEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityEntryService {
    private final ActivityEntryRepository activityEntryRepository;

    public ActivityEntryService(ActivityEntryRepository activityEntryRepository) {
        this.activityEntryRepository = activityEntryRepository;
    }

    public Optional<ActivityEntry> findOpenByWorkDayId(Long workDayId) {
        return activityEntryRepository.findFirstByWorkDayIdAndFinishedAtIsNull(workDayId);
    }

    public List<ActivityEntry> findByWorkDayId(Long workDayId) {
        return activityEntryRepository.findAllByWorkDayIdOrderByStartedAtAsc(workDayId);
    }

    public ActivityEntry save(ActivityEntry entry) {
        return activityEntryRepository.save(entry);
    }
}
