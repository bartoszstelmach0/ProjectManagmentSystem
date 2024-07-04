package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import com.example.projectmanagementsystem.domain.task.Task;
import com.example.projectmanagementsystem.domain.task.TaskDtoMapper;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDtoMapperTest {

    private ProjectDtoMapper projectDtoMapper;
    @Mock
    TaskDtoMapper taskDtoMapper;
    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        projectDtoMapper = new ProjectDtoMapper(taskDtoMapper, userRepository);
    }

    @Test
     void shouldCorrectMapToDto(){
        //given
        Task task = new Task();
        User user = new User();
        TaskDto taskDto = new TaskDto();
        Project project = Project.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusHours(3))
                .tasks(Collections.singletonList(task))
                .user(user)
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
        assertEquals(projectDTO.getUserId(),project.getUser().getId());
    }

    @Test
    void shouldCorrectMapToEntity(){
        //given
        TaskDto taskdto = new TaskDto();
        Task task = new Task();
        User user = new User();
        user.setId(1L);
        ProjectDTO projectDto = ProjectDTO.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusHours(3))
                .tasks(Collections.singletonList(taskdto))
                .userId(user.getId())
                .build();

        Mockito.when(taskDtoMapper.map(taskdto)).thenReturn(task);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
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
        assertEquals(result.getUser(),user);
    }

}