package com.pitercoding.backend.service;

import com.pitercoding.backend.model.Program;
import com.pitercoding.backend.repository.ProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramService {
    private final ProgramRepository programRepository;

    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public List<Program> getActivePrograms() {
        return programRepository.findAllByActiveTrue();
    }

    public Program getById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Program not found: " + id));
    }
}
