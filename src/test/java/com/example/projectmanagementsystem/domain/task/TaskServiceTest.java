package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskDtoMapper mapper;

    private TaskService taskService;

    @BeforeEach
    public void init (){
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository,mapper);
    }

    @Test
    void shouldReturnAllTasks(){
        //given
        Task task = new Task();
        TaskDto taskDto = new TaskDto();

        Mockito.when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));
        Mockito.when(mapper.map(task)).thenReturn(taskDto);

        //when
        List<TaskDto> result = taskService.getAllTasks();
        //then
        assertNotNull(result);
        assertEquals(taskDto,result.get(0));
        assertEquals(1,result.size());

        Mockito.verify(taskRepository).findAll();
        Mockito.verify(mapper).map(task);
    }

    @Test
    void shouldReturnEmptyProject(){
        //given
        Mockito.when(taskRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<TaskDto> result = taskService.getAllTasks();
        //then
        assertNotNull(result);
        assertEquals(0,result.size());

        Mockito.verify(taskRepository).findAll();
        Mockito.verify(mapper,Mockito.never()).map(Mockito.any(Task.class));
    }

    @Test
    void shouldGetTaskByProjectIdCorrectly(){
        //given
        Long projectId = 1L;
        Task task = new Task();
        TaskDto taskDto = new TaskDto();

        Mockito.when(taskRepository.findByProjectId(projectId)).thenReturn(Collections.singletonList(task));
        Mockito.when(mapper.map(task)).thenReturn(taskDto);

        //when
        List<TaskDto> result = taskService.getTaskByProjectId(projectId);
        //then
        assertNotNull(result);
        assertEquals(taskDto,result.get(0));
        assertEquals(1,result.size());

        Mockito.verify(taskRepository).findByProjectId(projectId);
        Mockito.verify(mapper).map(task);
    }
    @Test
    void shouldNotReturnAnyTask(){
        //given
        Long projectId = 1L;

        Mockito.when(taskRepository.findByProjectId(projectId)).thenReturn(Collections.emptyList());

        //when
        List<TaskDto> result = taskService.getTaskByProjectId(projectId);
        //then
        assertNotNull(result);
        assertEquals(0,result.size());

        Mockito.verify(taskRepository).findByProjectId(projectId);
        Mockito.verify(mapper,Mockito.never()).map(Mockito.any(Task.class));
    }

    @Test
    void shouldGetTaskByStatusCorrectly(){

        //given
        String status = "TO_DO";
        Task.TaskStatus taskStatus = Task.TaskStatus.TO_DO;
        Task task = new Task();
        TaskDto taskDto = new TaskDto();

        Mockito.when(taskRepository.findByStatus(taskStatus)).thenReturn(Collections.singletonList(task));
        Mockito.when(mapper.map(task)).thenReturn(taskDto);

        //when
        List<TaskDto> result = taskService.getTaskByStatus(status);

        //then
        assertNotNull(result);
        assertEquals(taskDto,result.get(0));
        assertEquals(1,result.size());

        Mockito.verify(taskRepository).findByStatus(taskStatus);
        Mockito.verify(mapper).map(task);
    }

    @Test
    void shouldThrowExceptionForNullStatus(){
        //given
        String status = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTaskByStatus(status));
        assertEquals("Status cannot be null or empty!",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionForEmptyStatus(){
        //given
        String status = "";
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTaskByStatus(status));
        assertEquals("Status cannot be null or empty!",exception.getMessage());
    }

    @Test
    void shouldGetTaskByIdCorrectly(){
        //given
        Long taskId = 1L;
        Task task = new Task();
        TaskDto taskDto = new TaskDto();

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(mapper.map(task)).thenReturn(taskDto);
        //when
        Optional<TaskDto> result = taskService.getTaskById(taskId);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(taskDto,result.get());

        Mockito.verify(taskRepository).findById(taskId);
        Mockito.verify(mapper).map(task);
    }

    @Test
    void shouldCreateTaskCorrectly(){
        //given
        TaskDto taskDto = new TaskDto();
        Task taskToSave = new Task();
        Task savedTask = new Task();
        TaskDto savedTaskDto = new TaskDto();

        Mockito.when(mapper.map(taskDto)).thenReturn(taskToSave);
        Mockito.when(taskRepository.save(taskToSave)).thenReturn(savedTask);
        Mockito.when(mapper.map(savedTask)).thenReturn(savedTaskDto);

        //when
        TaskDto result = taskService.createTask(taskDto);
        //then
        assertNotNull(result);
        assertEquals(savedTaskDto,result);

        Mockito.verify(mapper).map(taskDto);
        Mockito.verify(taskRepository).save(taskToSave);
        Mockito.verify(mapper).map(savedTask);
    }

    @Test
    void shouldThrowExceptionWhenWeAreCreatingNewTask(){
        //given
        TaskDto taskDto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDto));
        assertEquals("Task dto cannot be null!",exception.getMessage());
    }

    @Test
    void shouldUpdateTaskCorrectly(){
        //given
        Long taskId = 1L;
        TaskDto basicDto = new TaskDto();
        basicDto.setName("Updated Task name");
        TaskDto updatedTaskDto = new TaskDto();
        updatedTaskDto.setName("Updated Task name");
        Task task = new Task();
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task name");


        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(taskRepository.save(task)).thenReturn(updatedTask);
        Mockito.when(mapper.map(updatedTask)).thenReturn(updatedTaskDto);
        //when
        Optional<TaskDto> result = taskService.updateTask(taskId, basicDto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(updatedTaskDto,result.get());
        assertEquals("Updated Task name",task.getName());

        Mockito.verify(taskRepository).findById(taskId);
        Mockito.verify(taskRepository).save(task);
        Mockito.verify(mapper).map(task);
    }

    @Test
    void shouldUpdateTaskCorrectlyWithRightDescription(){
        //given
        Long taskId = 1L;
        TaskDto basicDto = new TaskDto();
        basicDto.setDescription("Updated Description");
        TaskDto updatedTaskDto = new TaskDto();
        updatedTaskDto.setDescription("Updated Description");
        Task task = new Task();
        Task updatedTask = new Task();
        updatedTask.setDescription("Updated Description");


        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(taskRepository.save(task)).thenReturn(updatedTask);
        Mockito.when(mapper.map(updatedTask)).thenReturn(updatedTaskDto);
        //when
        Optional<TaskDto> result = taskService.updateTask(taskId, basicDto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(updatedTaskDto,result.get());
        assertEquals("Updated Description",task.getDescription());

        Mockito.verify(taskRepository).findById(taskId);
        Mockito.verify(taskRepository).save(task);
        Mockito.verify(mapper).map(task);
    }
    @Test
    void shouldUpdateTaskCorrectlyWithRightDeadline(){
        //given
        Long taskId = 1L;
        TaskDto basicDto = new TaskDto();
        LocalDateTime newDeadline = LocalDateTime.now().plusDays(1);
        basicDto.setDeadline(newDeadline);
        TaskDto updatedTaskDto = new TaskDto();
        updatedTaskDto.setDeadline(newDeadline);
        Task task = new Task();
        Task updatedTask = new Task();
        updatedTask.setDeadline(newDeadline);


        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(taskRepository.save(task)).thenReturn(updatedTask);
        Mockito.when(mapper.map(updatedTask)).thenReturn(updatedTaskDto);
        //when
        Optional<TaskDto> result = taskService.updateTask(taskId, basicDto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(updatedTaskDto,result.get());
        assertEquals(newDeadline,task.getDeadline());

        Mockito.verify(taskRepository).findById(taskId);
        Mockito.verify(taskRepository).save(task);
        Mockito.verify(mapper).map(task);
    }
    @Test
    void shouldUpdateTaskCorrectlyWithRightStatus(){
        //given
        Long taskId = 1L;
        TaskDto basicDto = new TaskDto();
        basicDto.setStatus("IN_PROGRESS");
        TaskDto updatedTaskDto = new TaskDto();
        updatedTaskDto.setStatus("IN_PROGRESS");
        Task task = new Task();
        Task updatedTask = new Task();
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);


        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(taskRepository.save(task)).thenReturn(updatedTask);
        Mockito.when(mapper.map(updatedTask)).thenReturn(updatedTaskDto);
        //when
        Optional<TaskDto> result = taskService.updateTask(taskId, basicDto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(updatedTaskDto,result.get());
        assertEquals(Task.TaskStatus.IN_PROGRESS,task.getStatus());

        Mockito.verify(taskRepository).findById(taskId);
        Mockito.verify(taskRepository).save(task);
        Mockito.verify(mapper).map(task);
    }

    @Test
    void shouldThrowExceptionWhenParametersAreInvalid(){
        //given
        Long invalidTaskId = -1L;
        TaskDto dto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(invalidTaskId, dto));
        assertEquals("Invalid parameters",exception.getMessage());
    }
    @Test
    void shouldDeleteTaskCorrectly(){
        //given
        Long taskId = 1L;
        //when + then
        taskService.deleteTask(taskId);
        Mockito.verify(taskRepository).deleteById(taskId);
    }
    @Test
    void shouldThrowExceptionWhileTryingToDeleteTask(){
        //given
        Long taskId = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(taskId));
        assertEquals("Argument is not valid!",exception.getMessage());
    }
}