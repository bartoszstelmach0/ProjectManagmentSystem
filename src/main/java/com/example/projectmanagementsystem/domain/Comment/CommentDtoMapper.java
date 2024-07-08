package com.example.projectmanagementsystem.domain.Comment;

import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentDtoMapper {

        private final UserRepository userRepository;
        private final ProjectRepository projectRepository;
        private final TaskRepository taskRepository;

    public CommentDtoMapper(UserRepository userRepository, ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public CommentDto map (Comment comment){
        return  CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userId(comment.getUser().getId())
                .projectId(comment.getProject().getId())
                .taskId(comment.getTask().getId())
                .build();
    }

    public Comment map (CommentDto dto){
        return  Comment.builder()
                .id(dto.getId())
                .comment(dto.getComment())
                .user(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found!")))
                .project(dto.getProjectId() != null ? projectRepository.findById(dto.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Project not found!")):null)
                .task(dto.getTaskId() != null ? taskRepository.findById(dto.getTaskId())
                        .orElseThrow(() -> new RuntimeException("Task not found!")):null)
                .build();
    }
}
