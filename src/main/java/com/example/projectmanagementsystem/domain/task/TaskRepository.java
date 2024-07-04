package com.example.projectmanagementsystem.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByProjectId (Long projectId);
    List<Task> findByStatus(Task.TaskStatus status);
    public Optional<Task> findByName (String name);
}
