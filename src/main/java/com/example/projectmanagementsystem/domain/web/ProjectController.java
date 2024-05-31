package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.project.ProjectService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
}
