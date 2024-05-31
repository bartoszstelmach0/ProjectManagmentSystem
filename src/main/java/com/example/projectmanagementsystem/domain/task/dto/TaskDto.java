package com.example.projectmanagementsystem.domain.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private String status;
}
