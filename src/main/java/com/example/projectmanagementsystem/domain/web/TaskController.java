package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.task.TaskService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


}
