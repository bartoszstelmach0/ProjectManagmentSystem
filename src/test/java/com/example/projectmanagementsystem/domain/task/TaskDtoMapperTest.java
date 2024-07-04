package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
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

    @Mock
    UserRepository userRepository;
    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        taskDtoMapper = new TaskDtoMapper(projectRepository, userRepository);
    }

    @Test
    public void shouldCorrectlyMapToDto(){
        // given
        Project project = new Project();
        project.setId(1L);

        User user = new User();
        user.setId(1L);

        Task task = Task.builder()
                .id(1L)
                .name("Test task")
                .description("Test description")
                .deadline(LocalDateTime.now())
                .status(Task.TaskStatus.TO_DO)
                .project(project)
                .user(user)
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
        assertEquals(task.getUser().getId(),taskDto.getUserId());
    }
    @Test
    public void shouldCorrectlyMapToEntity(){
        //given
        Long projectId = 1L;
        Long userId = 1L;

        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test task")
                .description("Test description")
                .deadline(LocalDateTime.now())
                .status("TO_DO")
                .projectId(projectId)
                .userId(userId)
                .build();

        Project project = new Project();
        project.setId(projectId);

        User user = new User();
        user.setId(userId);
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //when
        Task task = taskDtoMapper.map(taskDto);
        //then
        assertNotNull(task);
        assertEquals(taskDto.getId(),task.getId());
        assertEquals(taskDto.getName(),task.getName());
        assertEquals(taskDto.getDescription(),task.getDescription());
        assertEquals(Task.TaskStatus.TO_DO,task.getStatus());
        assertEquals(project,task.getProject());
        assertEquals(user,task.getUser());
    }
}