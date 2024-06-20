package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskDtoMapperTest {

    private TaskDtoMapper taskDtoMapper;
    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        taskDtoMapper = new TaskDtoMapper(projectRepository);
    }

    @Test
    public void shouldCorrectlyMapToDto(){
        // given
        Project project = new Project();
        Task task = Task.builder()
                .id(1L)
                .name("Test task")
                .description("Test description")
                .deadline(LocalDateTime.now())
                .status(Task.TaskStatus.TO_DO)
                .project(project)
                .build();
        //  when
        TaskDto taskDto = taskDtoMapper.map(task);
        //then
        assertNotNull(taskDto);
        assertEquals(task.getId(),taskDto.getId());
        assertEquals(task.getName(),taskDto.getName());
        assertEquals(task.getDescription(),taskDto.getDescription());
        assertEquals(task.getDeadline(),taskDto.getDeadline());
        assertEquals(task.getStatus().toString(),taskDto.getStatus());
        assertEquals(task.getProject().getId(),taskDto.getProjectId());
    }
    @Test
    public void shouldCorrectlyMapToEntity(){
        //given
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test task")
                .description("Test description")
                .deadline(LocalDateTime.now())
                .status("TO_DO")
                .projectId(1L)
                .build();

        Project project = new Project();
        Long projectId = 1L;
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        //when
        Task task = taskDtoMapper.map(taskDto);
        //then
        assertNotNull(task);
        assertEquals(taskDto.getId(),task.getId());
        assertEquals(taskDto.getName(),task.getName());
        assertEquals(taskDto.getDescription(),task.getDescription());
        assertEquals(Task.TaskStatus.TO_DO,task.getStatus());
        assertEquals(project,task.getProject());
    }
}