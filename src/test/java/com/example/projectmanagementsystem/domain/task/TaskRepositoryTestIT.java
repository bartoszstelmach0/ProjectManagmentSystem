package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.Comment.Comment;
import com.example.projectmanagementsystem.domain.Comment.CommentDtoMapper;
import com.example.projectmanagementsystem.domain.Comment.CommentRepository;
import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TaskRepositoryTestIT {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;

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
    void shouldFindByProjectIdCorrectly(){
        //when
        List<Task> result = taskRepository.findByProjectId(defaultProject.getId());
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
       assertEquals(result.get(0).getProject().getId(),defaultProject.getId());
    }

    @Test
    void shouldFindByStatusCorrectly(){
        //when
        List<Task> result = taskRepository.findByStatus(defaultTask.getStatus());
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(result.get(0).getStatus(),defaultTask.getStatus());
    }

    @Test
    void shouldFindByNameCorrectly(){
        //when
        Optional<Task> result = taskRepository.findByName(defaultTask.getName());
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getName(),defaultTask.getName());
    }

}