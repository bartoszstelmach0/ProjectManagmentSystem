package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import com.example.projectmanagementsystem.domain.task.TaskDtoMapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ProjectDtoMapper {

    private final TaskDtoMapper taskDtoMapper;
    private final UserRepository userRepository;
    public ProjectDtoMapper(TaskDtoMapper taskDtoMapper, UserRepository userRepository) {
        this.taskDtoMapper = taskDtoMapper;
        this.userRepository = userRepository;
    }

    public ProjectDTO map (Project project){
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .tasks(project.getTasks().stream().map(taskDtoMapper::map).collect(Collectors.toList()))
                .userId(project.getUser().getId())
                .build();
    }

    public Project map (ProjectDTO dto){
        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .tasks(dto.getTasks().stream().map(taskDtoMapper::map).collect(Collectors.toList()))
                .user(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found with id " + dto.getUserId())))
                .build();
    }
}
