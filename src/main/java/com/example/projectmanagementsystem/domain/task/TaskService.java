package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskDtoMapper mapper;

    public TaskService(TaskRepository taskRepository, TaskDtoMapper mapper) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    public List<TaskDto> getAllTasks(){
        return taskRepository.findAll().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTaskByProjectId(Long projectId){
        return taskRepository.findByProjectId(projectId).stream()
                .map(mapper::map).collect(Collectors.toList());
    }

    public List<TaskDto> getTaskByStatus (String status){
        if(status == null || status.isEmpty()){
            throw new IllegalArgumentException("Status cannot be null or empty!");
        }
        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status);
        return taskRepository.findByStatus(taskStatus)
               .stream()
               .map(mapper::map)
               .collect(Collectors.toList());
    }

    public Optional<TaskDto> getTaskById(Long taskId){
        return taskRepository.findById(taskId).map(mapper::map);
    }

    public TaskDto createTask (TaskDto dto){
        if (dto == null){
            throw new IllegalArgumentException("Task dto cannot be null!");
        }

        Task taskToSave = mapper.map(dto);
        Task savedTask = taskRepository.save(taskToSave);
        return mapper.map(savedTask);
    }

    public Optional<TaskDto> updateTask (Long taskId , TaskDto dto){
        if(taskId == null || taskId < 0 || dto == null ){
            throw new IllegalArgumentException("Invalid parameters");
        }
        return taskRepository.findById(taskId)
                .map(existingTask -> {
                    if (dto.getName() != null){
                        existingTask.setName(dto.getName());
                    }
                    if(dto.getDescription() != null){
                        existingTask.setDescription(dto.getDescription());
                    }
                    if (dto.getDeadline() != null){
                        existingTask.setDeadline(dto.getDeadline());
                    }
                    if(dto.getStatus() != null){
                        existingTask.setStatus(Task.TaskStatus.valueOf(dto.getStatus()));
                    }
                    Task updatedTask = taskRepository.save(existingTask);
                   return mapper.map(updatedTask);
                });
    }

    public void deleteTask(Long taskId){
        if (taskId == null || taskId < 0){
            throw new IllegalArgumentException("Argument is not valid!");
        }
        taskRepository.deleteById(taskId);
    }

}
