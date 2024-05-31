package com.example.projectmanagementsystem.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository  extends JpaRepository<Project,Long> {

    List<Project> findByNameContainingIgnoreCase (String name);
    List<Project> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Project> findByEndDateBetween(LocalDateTime start, LocalDateTime end);

}
