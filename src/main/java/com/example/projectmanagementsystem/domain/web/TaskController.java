package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.task.TaskService;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    public TaskController(TaskService taskService, ObjectMapper objectMapper) {
        this.taskService = taskService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    ResponseEntity<List<TaskDto>> getAllTasks(){
        List<TaskDto> result = taskService.getAllTasks();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/projectId/{id}")
    ResponseEntity<List<TaskDto>> getTaskByProjectId(@PathVariable Long id){
        List<TaskDto> result = taskService.getTaskByProjectId(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    ResponseEntity<List<TaskDto>> getTaskByStatus (@RequestParam String status){
        try{
            List<TaskDto> result = taskService.getTaskByStatus(status);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<TaskDto> getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<TaskDto> createTask (@RequestBody @Valid TaskDto taskDto){
        try{
            TaskDto task = taskService.createTask(taskDto);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(task.getId())
                    .toUri();
            return  ResponseEntity.created(uri).body(task);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateTask (@PathVariable Long id, @RequestBody JsonMergePatch patch){
        try {
            TaskDto taskToUpdate = taskService.getTaskById(id).orElseThrow();
            TaskDto patchedTask = applyPatch(taskToUpdate, patch);
            taskService.updateTask(id, patchedTask);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid patch");
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    private TaskDto applyPatch(TaskDto taskToUpdate, JsonMergePatch patch) throws JsonProcessingException, JsonPatchException {
        JsonNode jsonNode = objectMapper.valueToTree(taskToUpdate);
        JsonNode taskPatchNode = patch.apply(jsonNode);
        return objectMapper.treeToValue(taskPatchNode,TaskDto.class);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTaskById(@PathVariable Long id){
        try{
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
