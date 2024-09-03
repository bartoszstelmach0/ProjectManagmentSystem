package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.Comment.Comment;
import com.example.projectmanagementsystem.domain.Comment.CommentDtoMapper;
import com.example.projectmanagementsystem.domain.Comment.CommentRepository;
import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceTestIT {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TaskDtoMapper taskDtoMapper;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentDtoMapper commentDtoMapper;
    @Autowired
    TaskService taskService;

    private Project defaultProject;
    private Task defaultTask;
    private User defaultUser;
    private Comment defaultComment;

    @BeforeEach
    public void init() {
        defaultUser = User.builder()
                .username("User")
                .email("example@example.com")
                .password("1222")
                .build();
        userRepository.save(defaultUser);

        defaultProject = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .user(defaultUser)
                .build();
        projectRepository.save(defaultProject);

        defaultTask = Task.builder()
                .name("task")
                .description("task description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status(Task.TaskStatus.TO_DO)
                .project(defaultProject)
                .user(defaultUser)
                .comments(new ArrayList<>())
                .build();
        taskRepository.save(defaultTask);

        defaultComment = Comment.builder()
                .comment("comment")
                .user(defaultUser)
                .project(defaultProject)
                .task(defaultTask)
                .build();
        commentRepository.save(defaultComment);

        defaultTask.getComments().add(defaultComment);
        taskRepository.save(defaultTask);
    }

    @Test
    void shouldGetAllTasksCorrectly() {
        // when
        List<TaskDto> result = taskService.getAllTasks();
        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(defaultTask.getId(), result.get(0).getId());
        assertEquals(defaultTask.getName(), result.get(0).getName());
        assertEquals(defaultTask.getDescription(), result.get(0).getDescription());
        assertEquals(defaultTask.getDeadline(), result.get(0).getDeadline());
        assertEquals("TO_DO", result.get(0).getStatus());
        assertEquals(defaultTask.getProject().getId(), result.get(0).getProjectId());
        assertEquals(defaultTask.getUser().getId(), result.get(0).getUserId());

        CommentDto commentDto = commentDtoMapper.map(defaultComment);
        assertEquals(commentDto.getComment(), result.get(0).getComments().get(0).getComment());
    }

    @Test
    void shouldGetTaskByProjectIdCorrectly() {
        // when
        List<TaskDto> result = taskService.getTaskByProjectId(defaultProject.getId());
        // then
        assertFalse(result.isEmpty());
        assertEquals(defaultTask.getId(), result.get(0).getId());
        assertEquals(defaultTask.getName(), result.get(0).getName());
        assertEquals(defaultTask.getDescription(), result.get(0).getDescription());
        assertEquals(defaultTask.getDeadline(), result.get(0).getDeadline());
        assertEquals("TO_DO", result.get(0).getStatus());
        assertEquals(defaultTask.getProject().getId(), result.get(0).getProjectId());
        assertEquals(defaultTask.getUser().getId(), result.get(0).getUserId());

        CommentDto commentDto = commentDtoMapper.map(defaultComment);
        assertEquals(commentDto.getComment(), result.get(0).getComments().get(0).getComment());
    }

    @Test
    void shouldGetTaskByStatus() {
        // given
        String status = "TO_DO";
        // when
        List<TaskDto> result = taskService.getTaskByStatus(status);
        // then
        assertFalse(result.isEmpty());
        assertEquals(defaultTask.getId(), result.get(0).getId());
        assertEquals(defaultTask.getName(), result.get(0).getName());
        assertEquals(defaultTask.getDescription(), result.get(0).getDescription());
        assertEquals(defaultTask.getDeadline(), result.get(0).getDeadline());
        assertEquals("TO_DO", result.get(0).getStatus());
        assertEquals(defaultTask.getProject().getId(), result.get(0).getProjectId());
        assertEquals(defaultTask.getUser().getId(), result.get(0).getUserId());

        CommentDto commentDto = commentDtoMapper.map(defaultComment);
        assertEquals(commentDto.getComment(), result.get(0).getComments().get(0).getComment());
    }

    @Test
    void shouldThrowExceptionWhenStatusIsNull() {
        // given
        String status = null;
        // when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTaskByStatus(status));
        assertEquals("Status cannot be null or empty!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStatusIsEmpty() {
        // given
        String status = "";
        // when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTaskByStatus(status));
        assertEquals("Status cannot be null or empty!", exception.getMessage());
    }

    @Test
    void shouldGetTaskByIdCorrectly(){
        //when
        Optional<TaskDto> result = taskService.getTaskById(defaultTask.getId());
        //then
        assertTrue(result.isPresent());
        assertEquals(defaultTask.getId(), result.get().getId());
        assertEquals(defaultTask.getName(), result.get().getName());
        assertEquals(defaultTask.getDescription(), result.get().getDescription());
        assertEquals(defaultTask.getDeadline(), result.get().getDeadline());
        assertEquals("TO_DO", result.get().getStatus());
        assertEquals(defaultTask.getProject().getId(), result.get().getProjectId());
        assertEquals(defaultTask.getUser().getId(), result.get().getUserId());

        CommentDto commentDto = commentDtoMapper.map(defaultComment);
        assertEquals(commentDto.getComment(), result.get().getComments().get(0).getComment());
    }

    @Test
    void shouldCreateTaskCorrectly(){
        //given
        CommentDto commentDto = new CommentDto();
        commentDto.setComment("comment");
        commentDto.setUserId(defaultUser.getId());
        commentDto.setProjectId(defaultProject.getId());
        commentDto.setTaskId(defaultTask.getId());

        TaskDto taskDto = TaskDto.builder()
                .name("test")
                .description("description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .projectId(defaultProject.getId())
                .userId(defaultUser.getId())
                .comments(Collections.singletonList(commentDto))
                .build();
        //when
        TaskDto result = taskService.createTask(taskDto);
        //then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(taskDto.getName(),result.getName());
        assertEquals(taskDto.getProjectId(),result.getProjectId());
        assertEquals(taskDto.getUserId(),result.getUserId());
    }

    @Test
    void shouldThrowExceptionWhenTaskDtoIsNull(){
        //given
        TaskDto dto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(dto));
        assertEquals("Task dto cannot be null!",exception.getMessage());
    }

    @Test
    void shouldUpdateTaskCorrectly(){
        //given
        CommentDto commentDto = new CommentDto();
        commentDto.setComment("comment");
        commentDto.setUserId(defaultUser.getId());
        commentDto.setProjectId(defaultProject.getId());
        commentDto.setTaskId(defaultTask.getId());

        TaskDto taskDto = TaskDto.builder()
                .name("test")
                .description("description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .projectId(defaultProject.getId())
                .userId(defaultUser.getId())
                .comments(Collections.singletonList(commentDto))
                .build();
        //when
        Optional<TaskDto> result = taskService.updateTask(defaultTask.getId(), taskDto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        assertEquals(taskDto.getName(),result.get().getName());
        assertEquals(taskDto.getProjectId(),result.get().getProjectId());
        assertEquals(taskDto.getUserId(),result.get().getUserId());
    }

    @Test
    void shouldThrowExceptionWhenTaskIdIsNullDurningUpdatingTask(){
        //given
        defaultTask.setId(null);

        CommentDto commentDto = new CommentDto();
        commentDto.setComment("comment");
        commentDto.setUserId(defaultUser.getId());
        commentDto.setProjectId(defaultProject.getId());
        commentDto.setTaskId(defaultTask.getId());

        TaskDto taskDto = TaskDto.builder()
                .name("test")
                .description("description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .projectId(defaultProject.getId())
                .userId(defaultUser.getId())
                .comments(Collections.singletonList(commentDto))
                .build();
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(defaultTask.getId(), taskDto));
        assertEquals("Invalid parameters",exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTaskIdIsNegativeNumberDurningUpdatingTask(){
        //given
        defaultTask.setId(-1L);

        CommentDto commentDto = new CommentDto();
        commentDto.setComment("comment");
        commentDto.setUserId(defaultUser.getId());
        commentDto.setProjectId(defaultProject.getId());
        commentDto.setTaskId(defaultTask.getId());

        TaskDto taskDto = TaskDto.builder()
                .name("test")
                .description("description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .projectId(defaultProject.getId())
                .userId(defaultUser.getId())
                .comments(Collections.singletonList(commentDto))
                .build();
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(defaultTask.getId(), taskDto));
        assertEquals("Invalid parameters",exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTaskDtoIsNullDurningUpdatingTask(){
        //given
        TaskDto taskDto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(defaultTask.getId(), taskDto));
        assertEquals("Invalid parameters",exception.getMessage());
    }

    @Test
    void shouldDeleteTaskCorrectly(){
        //given
        defaultTask.setId(1L);
        //when
        taskService.deleteTask(defaultTask.getId());
        //then
        Optional<Task> deletedTask = taskRepository.findById(defaultTask.getId());
        assertFalse(deletedTask.isPresent());
    }

    @Test
    void shouldThrowExceptionDurningDeletingTask(){
        //given
        defaultTask.setId(null);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(defaultTask.getId()));
        assertEquals("Argument is not valid!",exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDurningDeletingTaskWhenTaskIdIsNegativeNumber(){
        //given
        defaultTask.setId(-1L);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(defaultTask.getId()));
        assertEquals("Argument is not valid!",exception.getMessage());
    }
}