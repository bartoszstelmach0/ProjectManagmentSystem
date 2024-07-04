package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.ProjectService;
import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.json.*;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import javax.swing.text.html.Option;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    UserRepository userRepository;
    @MockBean
    private ProjectService projectService;


    @Test
    void shouldReturnAllProjects() throws Exception {
        //given
        Long userId = 1L;
        ProjectDTO projectDto = ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .description("Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(1))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();
        Mockito.when(projectService.getAllProjects()).thenReturn(Collections.singletonList(projectDto));
        //when + then
        mockMvc.perform(get("/project")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(projectDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(projectDto.getName())))
                .andExpect(jsonPath("$[0].description", is(projectDto.getDescription())));
    }

    @Test
    void shouldReturnEmptyListWhenNoProjects() throws Exception {
        //given
        Mockito.when(projectService.getAllProjects()).thenReturn(Collections.emptyList());

        //when + then
        mockMvc.perform(get("/project").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    void shouldReturnProjectById() throws Exception {
        //given
        Long id = 1L;
        Long userId = 1L;
        ProjectDTO projectDto = ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .description("Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(1))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();
        Mockito.when(projectService.getProjectById(id)).thenReturn(Optional.of(projectDto));
        //when + then

        mockMvc.perform(get("/project/{id}",id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(projectDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(projectDto.getName())))
                .andExpect(jsonPath("$.description",is(projectDto.getDescription())));

    }

    @Test
    void shouldReturnEmptyOptionalWhenProjectIsEmpty() throws Exception {
        //given
        Long id = 1L;
        Mockito.when(projectService.getProjectById(id)).thenReturn(Optional.empty());
        //when + then
        mockMvc.perform(get("/project/{id}",id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateTestCorrectly() throws Exception {
        //given
        Long userId = 1L;
        ProjectDTO projectDto = ProjectDTO.builder()
                .id(1L)
                .name("Test Project")
                .description("Description")
                .startDate(LocalDateTime.now().plusSeconds(1))
                .endDate(LocalDateTime.now().plusHours(1))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        Mockito.when(projectService.createNewProject(Mockito.any(ProjectDTO.class))).thenReturn(projectDto);
        //when + then

        mockMvc.perform(post("/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.containsString("/project/1")))
                .andExpect(jsonPath("$.id",is(projectDto.getId().intValue())))
                .andExpect(jsonPath("$.name",is(projectDto.getName())))
                .andExpect(jsonPath("$.description",is(projectDto.getDescription())));
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        //given
        Long userId = 1L;
        ProjectDTO projectDto = ProjectDTO.builder()
                .id(1L)
                .name(null)
                .description("Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(1))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();
        //when + then
        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPartiallyUpdateProject() throws Exception {
        //given
        Long projectId = 1L;
        Long userId = 1L;

        String patchContent = """
                {
                    "name": "Updated Project Name",
                    "description": "Updated Description"
                }""";

        ProjectDTO originalProject = ProjectDTO.builder()
                .id(projectId)
                .name("Original Name")
                .description("Original Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        ProjectDTO updatedProject = ProjectDTO.builder()
                .id(projectId)
                .name("Updated Project Name")
                .description("Updated Description")
                .startDate(originalProject.getStartDate())
                .endDate(originalProject.getEndDate())
                .tasks(originalProject.getTasks())
                .userId(userId)
                .build();

        Mockito.when(projectService.getProjectById(projectId)).thenReturn(Optional.of(originalProject));
        Mockito.when(projectService.updateProject(anyLong(), any(ProjectDTO.class))).thenReturn(Optional.of(updatedProject));

        //when + then

        mockMvc.perform(patch("/project/{id}", projectId)
                        .content(patchContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldHandleEmptyPatchContent() throws Exception {
        //given
        Long projectId = 1L;
        Long userId = 1L;

        String patchContent = """
                {
                   
                }""";

        ProjectDTO originalProject = ProjectDTO.builder()
                .id(projectId)
                .name("Original Name")
                .description("Original Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        Mockito.when(projectService.getProjectById(projectId)).thenReturn(Optional.of(originalProject));

        //when + then

        mockMvc.perform(patch("/project/{id}",projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchContent))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/project/{id}",projectId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",is(originalProject.getName())))
                .andExpect(jsonPath("$.description",is(originalProject.getDescription())));
    }

    @Test
    void shouldDeleteProjectSuccessfully() throws Exception {
        Long projectId = 1L;

        doNothing().when(projectService).deleteProject(projectId);

        mockMvc.perform(delete("/project/{id}", projectId))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProject(projectId);
    }

    @Test
    void shouldGetProjectByNameContainingCorrectly() throws Exception {
        //given

        String name = "Test";
        Long projectId = 1L;

        Long userId = 1L;
        ProjectDTO originalProject = ProjectDTO.builder()
                .id(projectId)
                .name("Original Name")
                .description("Original Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        Mockito.when(projectService.getProjectsByNameContaining(name)).thenReturn(Collections.singletonList(originalProject));

        //when + then
        mockMvc.perform(get("/project/name")
                        .param("name", name)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(originalProject.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(originalProject.getName())))
                .andExpect(jsonPath("$[0].description", is(originalProject.getDescription())))
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].endDate").exists());
    }

    @Test
    void shouldReturnBadRequestForNullArgument() throws Exception {
        String name = null;

        Mockito.when(projectService.getProjectsByNameContaining(name)).thenThrow(new IllegalArgumentException("Name cannot be null!"));
        //when + then
        mockMvc.perform(get("/project/name")
                .accept(MediaType.APPLICATION_JSON)
                .param("name",name))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldReturnBadRequestForMissingName() throws Exception {
        String name = "";
        Mockito.when(projectService.getProjectsByNameContaining(name)).thenThrow(new IllegalArgumentException("Name cannot be null!"));

        // when + then
        mockMvc.perform(get("/project/name")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnProjectByStartDateBetween() throws Exception {
        //given
        Long projectId = 1L;
        Long userId = 1L;
        LocalDateTime startDate = LocalDateTime.now().plusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(3);

        ProjectDTO originalProject = ProjectDTO.builder()
                .id(projectId)
                .name("Original Name")
                .description("Original Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        Mockito.when(projectService.getProjectByStartDateBetween(startDate,endDate)).thenReturn(Collections.singletonList(originalProject));
        //when+then
        mockMvc.perform(get("/project/startDate")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDate",startDate.toString())
                .param("endDate",endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(originalProject.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(originalProject.getName())))
                .andExpect(jsonPath("$[0].description", is(originalProject.getDescription())))
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].endDate").exists());
    }

    @Test
    void shouldThrowExceptionBecauseOfEndDateIsEarlierThanStartDate() throws Exception {
        //given
        Long projectId = 1L;
        LocalDateTime startDate = LocalDateTime.now().plusHours(3);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        Long userId = 1L;
        ProjectDTO originalProject = ProjectDTO.builder()
                .id(projectId)
                .name("Original Name")
                .description("Original Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        Mockito.when(projectService.getProjectByStartDateBetween(startDate,endDate)).thenThrow(new IllegalArgumentException("Start date cannot be after end date"));

        //when + then
        mockMvc.perform(get("/project/startDate")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate",startDate.toString())
                        .param("endDate",endDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnProjectByEndDateBetween() throws Exception {
        //given
        Long projectId = 1L;
        LocalDateTime startDate = LocalDateTime.now().plusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(3);
        Long userId = 1L;

        ProjectDTO originalProject = ProjectDTO.builder()
                .id(projectId)
                .name("Original Name")
                .description("Original Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        Mockito.when(projectService.getProjectByEndDateBetween(startDate,endDate)).thenReturn(Collections.singletonList(originalProject));
        //when+then
        mockMvc.perform(get("/project/endDate")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate",startDate.toString())
                        .param("endDate",endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(originalProject.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(originalProject.getName())))
                .andExpect(jsonPath("$[0].description", is(originalProject.getDescription())))
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].endDate").exists());
    }
    @Test
    void shouldThrowExceptionBecauseOfEndDateIsEarlierThanStartDateInEndingDate() throws Exception {
        //given
        Long projectId = 1L;
        LocalDateTime startDate = LocalDateTime.now().plusHours(3);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);
        Long userId = 1L;

        ProjectDTO originalProject = ProjectDTO.builder()
                .id(projectId)
                .name("Original Name")
                .description("Original Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .tasks(Collections.emptyList())
                .userId(userId)
                .build();

        Mockito.when(projectService.getProjectByEndDateBetween(startDate,endDate)).thenThrow(new IllegalArgumentException("Start date cannot be after end date"));

        //when + then
        mockMvc.perform(get("/project/endDate")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate",startDate.toString())
                        .param("endDate",endDate.toString()))
                .andExpect(status().isBadRequest());
    }
}