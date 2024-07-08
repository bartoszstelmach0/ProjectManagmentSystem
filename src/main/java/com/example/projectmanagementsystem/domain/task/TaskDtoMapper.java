package com.example.projectmanagementsystem.domain.task;


import com.example.projectmanagementsystem.domain.Comment.CommentDtoMapper;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TaskDtoMapper {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CommentDtoMapper commentDtoMapper;

    public TaskDtoMapper(ProjectRepository projectRepository, UserRepository userRepository, CommentDtoMapper commentDtoMapper) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.commentDtoMapper = commentDtoMapper;
    }

    public TaskDto map (Task task){
        return TaskDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .status(task.getStatus().toString())
                .projectId(task.getProject().getId())
                .userId(task.getUser().getId())
                .comments(task.getComments().stream().map(commentDtoMapper::map).collect(Collectors.toList()))
                .build();
    }

    public Task map (TaskDto dto){
        Task.TaskStatus status = Task.TaskStatus.valueOf(dto.getStatus());
        Project project = projectRepository.findById(dto.getProjectId()).orElseThrow();
        User user = userRepository.findById(dto.getUserId()).orElseThrow();
        return Task.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .deadline(dto.getDeadline())
                .status(status)
                .project(project)
                .user(user)
                .comments(dto.getComments().stream().map(commentDtoMapper::map).collect(Collectors.toList()))
                .build();
    }
}
