package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.Comment.Comment;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.project.Project;
import jakarta.persistence.*;
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

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Future
    private LocalDateTime deadline;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false,referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();
    public enum TaskStatus{
        TO_DO,
        IN_PROGRESS,
        DONE
    }
}
