package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import com.example.projectmanagementsystem.domain.task.TaskService;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TaskService taskService;


    @Test
    void shouldReturnAllProjects() throws Exception {
        //given
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        Mockito.when(taskService.getAllTasks()).thenReturn(Collections.singletonList(taskDto));
        //when + then

        mockMvc.perform(get("/task")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(taskDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(taskDto.getName())))
                .andExpect(jsonPath("$[0].description", is(taskDto.getDescription())))
                .andExpect(jsonPath("$[0].deadline", is(taskDto.getDeadline().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(taskDto.getStatus())));
    }

    @Test
    void shouldReturnEmptyListWithNoProjects() throws Exception {
        //given
        Mockito.when(taskService.getAllTasks()).thenReturn(Collections.emptyList());
        //when + then
        mockMvc.perform(get("/task")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    void shouldReturnProjectById() throws Exception {
        //given
        Long projectId = 1L;
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        Mockito.when(taskService.getTaskByProjectId(projectId)).thenReturn(Collections.singletonList(taskDto));
        //when + then

        mockMvc.perform(get("/task/projectId/{id}",projectId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(taskDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(taskDto.getName())))
                .andExpect(jsonPath("$[0].description", is(taskDto.getDescription())))
                .andExpect(jsonPath("$[0].deadline", is(taskDto.getDeadline().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(taskDto.getStatus())));
    }

    @Test
    void shouldReturnEmptyListBySearchingByProjectId() throws Exception {
        //given
        Long projectId = 1L;
        Mockito.when(taskService.getTaskByProjectId(projectId)).thenReturn(Collections.emptyList());

        //when + then
        mockMvc.perform(get("/task/projectId/{id}",projectId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    void shouldReturnTaskByStatus() throws Exception {
        //given
        String status = "TO_DO";
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        Mockito.when(taskService.getTaskByStatus(status)).thenReturn(Collections.singletonList(taskDto));
        // when + then
        mockMvc.perform(get("/task/status")
                        .param("status", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(taskDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(taskDto.getName())))
                .andExpect(jsonPath("$[0].description", is(taskDto.getDescription())))
                .andExpect(jsonPath("$[0].deadline", is(taskDto.getDeadline().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(taskDto.getStatus())));
    }

    @Test
    void shouldReturnEmptyListWhenStatusIsEmpty() throws Exception {
        //given
        String status = "";
        Mockito.doThrow(new IllegalArgumentException("Status cannot be null or empty!"))
                .when(taskService).getTaskByStatus(status);
        //when + then

        mockMvc.perform(get("/task/status")
                .param("status",status)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyListWhenStatusIsNull() throws Exception {
        //given
        String status = null;

        Mockito.doThrow(new IllegalArgumentException("Status cannot be null or empty!")).when(taskService).getTaskByStatus(status);
        //when + then

        mockMvc.perform(get("/task/status")
                        .param("status",status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTaskByIdCorrectly() throws Exception {
        //given
        Long taskId = 1L;
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        Mockito.when(taskService.getTaskById(taskId)).thenReturn(Optional.of(taskDto));
        //when + then

        mockMvc.perform(get("/task/{id}",taskId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id",is(taskDto.getId().intValue())))
                .andExpect(jsonPath("$.name",is(taskDto.getName())))
                .andExpect(jsonPath("$.description",is(taskDto.getDescription())))
                .andExpect(jsonPath("$.deadline",is(taskDto.getDeadline().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status",is(taskDto.getStatus())));
    }

    @Test
    void shouldReturnNotFoundForNonExistingTask() throws Exception {
        //given
        Long projectId = 1L;

        Mockito.when(taskService.getTaskById(projectId)).thenReturn(Optional.empty());

        //when + then
        mockMvc.perform(get("/task/{id}",projectId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateTaskCorrectly() throws Exception {
        //given
        Long projectId = 1L;
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        when(taskService.createTask(any(TaskDto.class))).thenReturn(taskDto);

        // when + then

        mockMvc.perform(post("/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.containsString("/task/1")))
                .andExpect(jsonPath("$.id",is(taskDto.getId().intValue())))
                .andExpect(jsonPath("$.name",is(taskDto.getName())))
                .andExpect(jsonPath("$.description",is(taskDto.getDescription())))
                .andExpect(jsonPath("$.deadline",is(taskDto.getDeadline().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status",is(taskDto.getStatus())));
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        //given
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name(null)
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();
        //when + then
        mockMvc.perform(post("/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateTaskCorrectly() throws Exception {
        //given
        Long taskId = 1L;

        String patchContent = """
                {
                    "name" : "Updated name",
                    "description" : "Update description"
                }
                """;

        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        TaskDto updatedTaskDto = TaskDto.builder()
                .id(1L)
                .name("Updated name")
                .description("Update description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        Mockito.when(taskService.getTaskById(taskId)).thenReturn(Optional.of(taskDto));
        Mockito.when(taskService.updateTask(taskId,taskDto)).thenReturn(Optional.of(updatedTaskDto));

        //when + then

        mockMvc.perform(patch("/task/{id}",taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchContent))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldHandleEmptyPatchContent() throws Exception {
        Long taskId = 1L;

        String patchContent = """
                {
                
                }
                """;

        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .deadline(LocalDateTime.now().plusHours(1))
                .status("TO_DO")
                .build();

        Mockito.when(taskService.getTaskById(taskId)).thenReturn(Optional.of(taskDto));

        //when + then
        mockMvc.perform(patch("/task/{id}",taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchContent))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/task/{id}",taskId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(taskDto.getName())))
                .andExpect(jsonPath("$.description",is(taskDto.getDescription())));
    }

    @Test
    void shouldDeleteTaskCorrectly() throws Exception {
        //given
        Long taskId = 1L;

        doNothing().when(taskService).deleteTask(taskId);
        //when + then

        mockMvc.perform(delete("/task/{id}",taskId))
                .andExpect(status().isNoContent());
    }
}