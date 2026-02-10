package com.pitercoding.backend.repository;

import com.pitercoding.backend.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language> findAllByActiveTrue();
}
