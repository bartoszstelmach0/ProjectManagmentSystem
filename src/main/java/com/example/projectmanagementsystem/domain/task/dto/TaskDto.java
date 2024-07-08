package com.example.projectmanagementsystem.domain.task.dto;

import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Future
    private LocalDateTime deadline;
    @NotNull
    private String status;
    private Long projectId;
    private Long userId;
    private List<CommentDto> comments = new ArrayList<>();

}
