package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.Comment.CommentService;
import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    CommentService commentService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllCommentsCorrectly() throws Exception {
        //given
        User user = new User();
        Project project = new Project();
        Task task = new Task();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .comment("example comment")
                .userId(user.getId())
                .projectId(project.getId())
                .taskId(task.getId())
                .build();

        Mockito.when(commentService.getAllComments()).thenReturn(Collections.singletonList(commentDto));

        //when + then
        mockMvc.perform(get("/comments").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$[0].comment",is(commentDto.getComment())))
                .andExpect(jsonPath("$[0].userId",is(commentDto.getUserId())))
                .andExpect(jsonPath("$[0].projectId",is(commentDto.getProjectId())))
                .andExpect(jsonPath("$[0].taskId",is(commentDto.getTaskId())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnEmptyListWhenListIsEmpty() throws Exception {
        //given
        Mockito.when(commentService.getAllComments()).thenReturn(Collections.emptyList());
        //when + then
        mockMvc.perform(get("/comments").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnCommentByIdCorrectly() throws Exception {
        //given
        Long commentId = 1L;
        User user = new User();
        Project project = new Project();
        Task task = new Task();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .comment("example comment")
                .userId(user.getId())
                .projectId(project.getId())
                .taskId(task.getId())
                .build();

        Mockito.when(commentService.findCommentById(commentId)).thenReturn(Optional.of(commentDto));
        //when + then
        mockMvc.perform(get("/comments/{id}",commentId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.comment",is(commentDto.getComment())))
                .andExpect(jsonPath("$.userId",is(commentDto.getUserId())))
                .andExpect(jsonPath("$.projectId",is(commentDto.getProjectId())))
                .andExpect(jsonPath("$.taskId",is(commentDto.getTaskId())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnNotFoundWhenOptionalIsNotFound() throws Exception {
        //given
        Long commentId = 1L;
        Mockito.when(commentService.findCommentById(commentId)).thenReturn(Optional.empty());
        //when + then
        mockMvc.perform(get("/comments/{id}",commentId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCommentCorrectly() throws Exception {
        //given
        Long commentId = 1L;
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

        Mockito.when(commentService.createComment(any(CommentDto.class))).thenReturn(commentDto);

        //when + then
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.containsString("/comments/1")))
                .andExpect(jsonPath("$.id",is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.comment",is(commentDto.getComment())))
                .andExpect(jsonPath("$.userId",is(commentDto.getUserId().intValue())))
                .andExpect(jsonPath("$.projectId",is(commentDto.getProjectId().intValue())))
                .andExpect(jsonPath("$.taskId",is(commentDto.getTaskId().intValue())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        //given
        Long commentId = 1L;
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .comment(null)
                .userId(user.getId())
                .projectId(project.getId())
                .taskId(task.getId())
                .build();
        //when + then
        mockMvc.perform(post("/comments")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnBadRequestWhenIllegalArgumentExceptionIsThrown() throws Exception {
        // given
        Long commentId = 1L;
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);
        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .comment("example comment")
                .userId(user.getId())
                .projectId(project.getId())
                .taskId(task.getId())
                .build();

        Mockito.when(commentService.createComment(any(CommentDto.class))).thenThrow(new IllegalArgumentException());
        // when + then
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnNoContentWhenCommentIsUpdated() throws Exception {
        //given
        Long commentId = 1L;
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .comment("example")
                .userId(user.getId())
                .projectId(project.getId())
                .taskId(task.getId())
                .build();

        Mockito.when(commentService.updateComment(eq(commentId),any(CommentDto.class))).thenReturn(Optional.of(commentDto));
        //when + then
        mockMvc.perform(put("/comments/{id}",commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnNotFoundWhenCommentDoesNotExist() throws Exception {
        //given
        Long commentId = 1L;
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .comment("example")
                .userId(user.getId())
                .projectId(project.getId())
                .taskId(task.getId())
                .build();

        Mockito.when(commentService.updateComment(eq(commentId),any(CommentDto.class))).thenReturn(Optional.empty());
        //when + then
        mockMvc.perform(put("/comments/{id}",commentId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
