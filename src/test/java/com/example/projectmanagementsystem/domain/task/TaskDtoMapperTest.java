package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.Comment.Comment;
import com.example.projectmanagementsystem.domain.Comment.CommentDtoMapper;
import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskDtoMapperTest {

    private TaskDtoMapper taskDtoMapper;
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    CommentDtoMapper commentDtoMapper;
    @Mock
    UserRepository userRepository;
    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        taskDtoMapper = new TaskDtoMapper(projectRepository, userRepository, commentDtoMapper);
    }

    @Test
    public void shouldCorrectlyMapToDto(){
        // given
        Project project = new Project();
        project.setId(1L);
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();

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
                .comments(Collections.singletonList(comment))
                .build();

        Mockito.when(commentDtoMapper.map(comment)).thenReturn(commentDto);
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
        assertEquals(taskDto.getComments().get(0),commentDto);
    }
    @Test
    public void shouldCorrectlyMapToEntity(){
        //given
        Long projectId = 1L;
        Long userId = 1L;

        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();

        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test task")
                .description("Test description")
                .deadline(LocalDateTime.now())
                .status("TO_DO")
                .projectId(projectId)
                .userId(userId)
                .comments(Collections.singletonList(commentDto))
                .build();

        Project project = new Project();
        project.setId(projectId);

        User user = new User();
        user.setId(userId);
        Mockito.when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(commentDtoMapper.map(commentDto)).thenReturn(comment);
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
        assertEquals(task.getComments().get(0),comment);
    }
}