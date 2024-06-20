package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import com.example.projectmanagementsystem.domain.task.Task;
import com.example.projectmanagementsystem.domain.task.TaskDtoMapper;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDtoMapperTest {

    private ProjectDtoMapper projectDtoMapper;
    @Mock
    TaskDtoMapper taskDtoMapper;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        projectDtoMapper = new ProjectDtoMapper(taskDtoMapper);
    }

    @Test
     void shouldCorrectMapToDto(){
        //given
        Task task = new Task();
        TaskDto taskDto = new TaskDto();
        Project project = Project.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusHours(3))
                .tasks(Collections.singletonList(task))
                .build();

        Mockito.when(taskDtoMapper.map(task)).thenReturn(taskDto);
        //when
        ProjectDTO projectDTO = projectDtoMapper.map(project);
        //then
        assertNotNull(projectDTO);
        assertEquals(project.getId(),projectDTO.getId());
        assertEquals(project.getDescription(),projectDTO.getDescription());
        assertEquals(project.getStartDate(),projectDTO.getStartDate());
        assertEquals(project.getEndDate(),projectDTO.getEndDate());
        assertEquals(taskDto, projectDTO.getTasks().get(0));
    }

    @Test
    void shouldCorrectMapToEntity(){
        //given
        TaskDto taskdto = new TaskDto();
        Task task = new Task();
        ProjectDTO projectDto = ProjectDTO.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusHours(3))
                .tasks(Collections.singletonList(taskdto))
                .build();

        Mockito.when(taskDtoMapper.map(taskdto)).thenReturn(task);
        //when
        Project result = projectDtoMapper.map(projectDto);
        //then
        assertNotNull(result);
        assertEquals(projectDto.getId(),result.getId());
        assertEquals(projectDto.getName(),result.getName());
        assertEquals(projectDto.getDescription(),result.getDescription());
        assertEquals(projectDto.getStartDate(),result.getStartDate());
        assertEquals(projectDto.getEndDate(),result.getEndDate());
        assertEquals(result.getTasks().get(0),task);
    }

}