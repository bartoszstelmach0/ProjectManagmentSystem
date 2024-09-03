package com.example.projectmanagementsystem.domain.User;

import com.example.projectmanagementsystem.domain.Comment.CommentDtoMapper;
import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.Role.RoleRepository;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.Task;
import com.example.projectmanagementsystem.domain.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDtoMapper {

    private final RoleRepository roleRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CommentDtoMapper commentDtoMapper;

    public UserDto map (User user){
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()))
                .projectNames(user.getProjects().stream().map(Project::getName).collect(Collectors.toList()))
                .taskNames(user.getTasks().stream().map(Task::getName).collect(Collectors.toList()))
                .comments(user.getComments().stream().map(commentDtoMapper::map).collect(Collectors.toList()))
                .build();
    }
    
    public User map(UserDto dto){
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .roles(dto.getRoles().stream()
                        .map(roleName -> roleRepository.findByName(Role.RoleName.valueOf(roleName))
                                .orElseThrow(() -> new RuntimeException("Role not found " + roleName)))
                        .collect(Collectors.toSet()))
                .projects(dto.getProjectNames().stream().map(project -> projectRepository.findByNameContainingIgnoreCase(project).stream().findFirst()
                        .orElseThrow(()-> new RuntimeException("Project not found!")))
                        .collect(Collectors.toList()))
                .tasks(dto.getTaskNames().stream().map(task -> taskRepository.findByName(task).orElseThrow(()-> new RuntimeException("Task not found")))
                        .collect(Collectors.toList()))
                .comments(dto.getComments().stream().map(commentDtoMapper::map).collect(Collectors.toList()))
                .build();
    }
}
