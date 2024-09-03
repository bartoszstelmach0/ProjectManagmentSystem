package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.Role.RoleService;
import com.example.projectmanagementsystem.domain.Role.dto.RoleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RoleService roleService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllRoles() throws Exception {
        //given
        RoleDto dto = RoleDto.builder().id(1L)
                .name("ROLE_ADMIN")
                .build();
        Mockito.when(roleService.getAllRoles()).thenReturn(Collections.singletonList(dto));
        //when + then
        mockMvc.perform(get("/roles").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(dto.getId().intValue())))
                .andExpect(jsonPath("$[0].name",is(dto.getName())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyList() throws Exception {
        //given
        Mockito.when(roleService.getAllRoles()).thenReturn(Collections.emptyList());
        //when + then
        mockMvc.perform(get("/roles").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnRoleById() throws Exception {
        //given
        RoleDto dto = RoleDto.builder().id(1L)
                .name("ROLE_ADMIN")
                .build();
        Long roleId = 1L;
        Mockito.when(roleService.getRoleById(roleId)).thenReturn(Optional.of(dto));
        //when + then
        mockMvc.perform(get("/roles/{id}",roleId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(dto.getId().intValue())))
                .andExpect(jsonPath("$.name",is(dto.getName())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyWhenDtoIsEmpty() throws Exception {
        //given
        Long roleId = 1L;
        Mockito.when(roleService.getRoleById(roleId)).thenReturn(Optional.empty());
        //when + then
        mockMvc.perform(get("/roles/{id}",roleId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}