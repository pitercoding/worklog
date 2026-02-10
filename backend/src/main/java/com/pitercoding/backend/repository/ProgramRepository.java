package com.pitercoding.backend.repository;

import com.pitercoding.backend.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findAllByActiveTrue();
}
