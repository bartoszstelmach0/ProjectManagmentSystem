package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.User.UserService;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;


    @Test
    void shouldReturnAllUsers() throws Exception {
        //given
        CommentDto commentDto = new CommentDto();

        UserDto dto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("example email")
                .password("123")
                .roles(Set.of("ROLE_ADMIN"))
                .projectNames(List.of("exmaple project"))
                .taskNames(List.of("example task"))
                .comments(Collections.singletonList(commentDto))
                .build();

        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(dto));
        //when + then
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(dto.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(dto.getUsername())))
                .andExpect(jsonPath("$[0].email",is(dto.getEmail())))
                .andExpect(jsonPath("$[0].password",is(dto.getPassword())))
                .andExpect(jsonPath("$[0].roles",hasItem("ROLE_ADMIN")))
                .andExpect(jsonPath("$[0].projectNames",is(dto.getProjectNames())))
                .andExpect(jsonPath("$[0].taskNames",is(dto.getTaskNames())))
                .andExpect(jsonPath("$[0].comments",hasSize(1)));
    }

    @Test
    void shouldReturnNoUsersWhenListIsEmpty() throws Exception {
        //given
            Mockito.when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        //when + then
            mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        //given
        CommentDto commentDto = new CommentDto();
        Long userId = 1L;

        UserDto dto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("example email")
                .password("123")
                .roles(Set.of("ROLE_ADMIN"))
                .projectNames(List.of("exmaple project"))
                .taskNames(List.of("example task"))
                .comments(Collections.singletonList(commentDto))
                .build();

        Mockito.when(userService.findUserById(userId)).thenReturn(Optional.of(dto));
        //when + then
        mockMvc.perform(get("/users/{id}",userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(dto.getId().intValue())))
                .andExpect(jsonPath("$.username", is(dto.getUsername())))
                .andExpect(jsonPath("$.email",is(dto.getEmail())))
                .andExpect(jsonPath("$.password",is(dto.getPassword())))
                .andExpect(jsonPath("$.roles",hasItem("ROLE_ADMIN")))
                .andExpect(jsonPath("$.projectNames",is(dto.getProjectNames())))
                .andExpect(jsonPath("$.taskNames",is(dto.getTaskNames())))
                .andExpect(jsonPath("$.comments",hasSize(1)));
    }

    @Test
    void shouldNotReturnAnyUserWhenDtoIsEmpty() throws Exception {
        //given
        Long userId = 1L;
        Mockito.when(userService.findUserById(userId)).thenReturn(Optional.empty());
        //when + then
        mockMvc.perform(get("/users/{id}",userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUserCorrectly() throws Exception {
        //given
        CommentDto commentDto = new CommentDto();

        UserDto dto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("email@email.com")
                .password("123")
                .roles(Set.of("ROLE_ADMIN"))
                .projectNames(List.of("exmaple project"))
                .taskNames(List.of("example task"))
                .comments(Collections.singletonList(commentDto))
                .build();

        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(dto);
        //when + then
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.containsString("/users/1")))
                .andExpect(jsonPath("$.id",is(dto.getId().intValue())))
                .andExpect(jsonPath("$.username", is(dto.getUsername())))
                .andExpect(jsonPath("$.email",is(dto.getEmail())))
                .andExpect(jsonPath("$.password",is(dto.getPassword())))
                .andExpect(jsonPath("$.roles",hasItem("ROLE_ADMIN")))
                .andExpect(jsonPath("$.projectNames",is(dto.getProjectNames())))
                .andExpect(jsonPath("$.taskNames",is(dto.getTaskNames())))
                .andExpect(jsonPath("$.comments",hasSize(1)));
    }
    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        //given
        CommentDto commentDto = new CommentDto();

        UserDto dto = UserDto.builder()
                .id(1L)
                .username("username")
                .email(null)
                .password("123")
                .roles(Set.of("ROLE_ADMIN"))
                .projectNames(List.of("exmaple project"))
                .taskNames(List.of("example task"))
                .comments(Collections.singletonList(commentDto))
                .build();
        //when + then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldDeleteCorrectly() throws Exception {
        //given
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);
        //when + then
        mockMvc.perform(delete("/users/{id}",userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateUserCorrectly() throws Exception {
        //given
        Long userId = 1L;
        CommentDto commentDto = new CommentDto();


        String patchContent = """
                {
                    "username": "Updated username"
                }
                """;
        UserDto originalDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .password("123")
                .roles(Set.of("ROLE_ADMIN"))
                .projectNames(List.of("exmaple project"))
                .taskNames(List.of("example task"))
                .comments(Collections.singletonList(commentDto))
                .build();

        UserDto updatedDto = UserDto.builder()
                .id(1L)
                .username("Updated username")
                .email("email@example.com")
                .password("123")
                .roles(Set.of("ROLE_ADMIN"))
                .projectNames(List.of("exmaple project"))
                .taskNames(List.of("example task"))
                .comments(Collections.singletonList(commentDto))
                .build();

        Mockito.when(userService.findUserById(userId)).thenReturn(Optional.of(originalDto));
        Mockito.when(userService.updateUser(userId,originalDto)).thenReturn(Optional.of(updatedDto));
        //when + then

        mockMvc.perform(patch("/users/{id}",userId)
                .content(patchContent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldHandleEmptyPatchContent() throws Exception {
        //given
        Long userId = 1L;
        CommentDto commentDto = new CommentDto();

        String patchContent = """
                {
                   
                }""";

        UserDto originalDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .password("123")
                .roles(Set.of("ROLE_ADMIN"))
                .projectNames(List.of("exmaple project"))
                .taskNames(List.of("example task"))
                .comments(Collections.singletonList(commentDto))
                .build();

        Mockito.when(userService.findUserById(userId)).thenReturn(Optional.of(originalDto));

        //when + then
        mockMvc.perform(patch("/users/{id}",userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchContent))
                .andExpect(status().isNoContent());
    }
}