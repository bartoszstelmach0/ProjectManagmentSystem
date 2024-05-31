package com.example.projectmanagementsystem.domain.task;


import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.springframework.stereotype.Service;

@Service
public class TaskDtoMapper {

    public TaskDto map (Task task){
        return TaskDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .status(task.getStatus().toString())
                .build();
    }

    public Task map (TaskDto dto){
        Task.TaskStatus status = null;
        status = Task.TaskStatus.valueOf(dto.getStatus());
        return Task.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .deadline(dto.getDeadline())
                .status(status)
                .build();
    }
}
