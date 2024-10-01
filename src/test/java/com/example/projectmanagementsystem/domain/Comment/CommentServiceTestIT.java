package com.example.projectmanagementsystem.domain.Comment;

import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.Role.RoleRepository;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.Task;
import com.example.projectmanagementsystem.domain.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTestIT {


    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentDtoMapper commentDtoMapper;
    @Autowired
    CommentService commentService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private User defaultUser;
    private Project defaultProject;
    private Task defaultTask;
    private Comment defaultComment;


    @BeforeEach
    public void init(){
        Role defaultRole = Role.builder()
                .name(Role.RoleName.ROLE_USER)
                .build();
        roleRepository.save(defaultRole);

        defaultUser = new User();
        defaultUser.setUsername("User");
        defaultUser.setEmail("example@example.com");
        defaultUser.setPassword(passwordEncoder.encode("122"));
        defaultUser.setRoles(Collections.singleton(defaultRole));
        userRepository.save(defaultUser);

        defaultProject = new Project();
        defaultProject.setName("Test Project");
        defaultProject.setDescription("Test Project Description");
        defaultProject.setStartDate(LocalDateTime.now().plusHours(1));
        defaultProject.setEndDate(LocalDateTime.now().plusDays(1));
        defaultProject.setUser(defaultUser);
        projectRepository.save(defaultProject);

        defaultTask = Task.builder()
                .name("task")
                .status(Task.TaskStatus.TO_DO)
                .deadline(LocalDateTime.now().plusDays(1))
                .project(defaultProject)
                .user(defaultUser)
                .build();
        taskRepository.save(defaultTask);

        defaultComment = Comment.builder()
                .comment("comment")
                .user(defaultUser)
                .project(defaultProject)
                .task(defaultTask)
                .build();
        commentRepository.save(defaultComment);
    }

    @Test
    void shouldGetAllCommentsCorrectly(){
        //when
        List<CommentDto> result = commentService.getAllComments();
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(defaultComment.getComment(),result.get(0).getComment());
        assertEquals(defaultComment.getUser().getId(),result.get(0).getUserId());
        assertEquals(defaultComment.getProject().getId(),result.get(0).getProjectId());
        assertEquals(defaultComment.getTask().getId(),result.get(0).getTaskId());
    }

    @Test
    void shouldFindCommentByIdCorrectly(){
        //given

        //when
        Optional<CommentDto> result = commentService.findCommentById(defaultComment.getId());
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(defaultComment.getComment(),result.get().getComment());
        assertEquals(defaultComment.getUser().getId(),result.get().getUserId());
        assertEquals(defaultComment.getProject().getId(),result.get().getProjectId());
        assertEquals(defaultComment.getTask().getId(),result.get().getTaskId());
    }

    @Test
    void shouldCreateCommentCorrectly(){
        //given
        CommentDto dto = CommentDto.builder()
                .comment("comment")
                .userId(defaultUser.getId())
                .projectId(defaultProject.getId())
                .taskId(defaultTask.getId())
                .build();
        //when
        CommentDto result = commentService.createComment(dto);
        //then
        assertNotNull(result);
        assertEquals(defaultComment.getComment(),result.getComment());
        assertEquals(defaultComment.getUser().getId(),result.getUserId());
        assertEquals(defaultComment.getProject().getId(),result.getProjectId());
        assertEquals(defaultComment.getTask().getId(),result.getTaskId());
    }

    @Test
    void shouldThrowExceptionDurningCreatingComment(){
        //given
        CommentDto  commentDto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commentService.createComment(commentDto));
        assertEquals("CommentDto cannot be null!",exception.getMessage());
    }

    @Test
    void shouldUpdateCommentCorrectly(){
        //given
        CommentDto dto = CommentDto.builder()
                .comment("comment")
                .userId(defaultUser.getId())
                .projectId(defaultProject.getId())
                .taskId(defaultTask.getId())
                .build();

        //when
        Optional<CommentDto> result = commentService.updateComment(defaultComment.getId(), dto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(defaultComment.getComment(),result.get().getComment());
        assertEquals(defaultComment.getUser().getId(),result.get().getUserId());
        assertEquals(defaultComment.getProject().getId(),result.get().getProjectId());
        assertEquals(defaultComment.getTask().getId(),result.get().getTaskId());
    }

    @Test
    void shouldReturnOptionalEmptyByIdNotExists(){
        //given
        CommentDto dto = CommentDto.builder()
                .comment("comment")
                .userId(defaultUser.getId())
                .projectId(defaultProject.getId())
                .taskId(defaultTask.getId())
                .build();

        Long notExistingId = 999L;
        //when
        Optional<CommentDto> result = commentService.updateComment(notExistingId, dto);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteCommentCorrectly(){
        //given
        defaultComment.setId(1L);
        //when
        commentService.deleteComment(defaultComment.getId());
        //then
        Optional<CommentDto> result = commentService.findCommentById(defaultComment.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionDurningDeletingComment(){
        //given
        defaultComment.setId(null);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(defaultComment.getId()));
        assertEquals("Id cannot be null!", exception.getMessage());
    }
}