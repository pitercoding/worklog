package com.pitercoding.backend.service;

import com.pitercoding.backend.model.Language;
import com.pitercoding.backend.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getActiveLanguages() {
        return languageRepository.findAllByActiveTrue();
    }

    public Language getById(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Language not found: " + id));
    }
}
