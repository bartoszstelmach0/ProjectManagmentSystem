package com.example.projectmanagementsystem.domain.Comment;

import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.Task;
import com.example.projectmanagementsystem.domain.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoMapperTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    TaskRepository taskRepository;

    private CommentDtoMapper commentDtoMapper;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        commentDtoMapper = new CommentDtoMapper(userRepository,projectRepository,taskRepository);
    }

    @Test
    public void shouldMapToDtoCorrectly(){
        //given
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);

        Comment exampleComment = Comment.builder()
                .id(1L)
                .comment("Example Comment")
                .user(user)
                .project(project)
                .task(task)
                .build();

        //when
        CommentDto result = commentDtoMapper.map(exampleComment);
        //then
        assertNotNull(result);
        assertEquals(result.getId(),exampleComment.getId());
        assertEquals(result.getComment(),exampleComment.getComment());
        assertEquals(result.getUserId(),exampleComment.getUser().getId());
        assertEquals(result.getProjectId(),exampleComment.getProject().getId());
        assertEquals(result.getTaskId(),exampleComment.getTask().getId());
    }

    @Test
    public void shouldCorrectlyMapToEntity(){
        //given
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .comment("example comment")
                .userId(user.getId())
                .projectId(project.getId())
                .taskId(task.getId())
                .build();

        Mockito.when(userRepository.findById(commentDto.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(projectRepository.findById(commentDto.getProjectId())).thenReturn(Optional.of(project));
        Mockito.when(taskRepository.findById(commentDto.getTaskId())).thenReturn(Optional.of(task));

        //when
        Comment result = commentDtoMapper.map(commentDto);
        //then
        assertNotNull(result);
        assertEquals(result.getId(),commentDto.getId());
        assertEquals(result.getComment(),commentDto.getComment());
        assertEquals(result.getProject().getId(),commentDto.getProjectId());
        assertEquals(result.getTask().getId(),commentDto.getTaskId());
    }
}