package com.example.projectmanagementsystem.domain.task;


import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.springframework.stereotype.Service;

@Service
public class TaskDtoMapper {

    private final ProjectRepository projectRepository;

    public TaskDtoMapper(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public TaskDto map (Task task){
        return TaskDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .status(task.getStatus().toString())
                .projectId(task.getProject().getId())
                .build();
    }

    public Task map (TaskDto dto){
        Task.TaskStatus status = Task.TaskStatus.valueOf(dto.getStatus());
        Project project = projectRepository.findById(dto.getProjectId()).orElseThrow();
        return Task.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .deadline(dto.getDeadline())
                .status(status)
                .project(project)
                .build();
    }
}
