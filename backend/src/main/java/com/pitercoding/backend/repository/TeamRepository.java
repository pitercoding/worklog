package com.pitercoding.backend.repository;

import com.pitercoding.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByActiveTrue();
}
