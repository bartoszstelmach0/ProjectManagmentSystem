package com.example.projectmanagementsystem.domain.web.Auth;

import com.example.projectmanagementsystem.domain.User.UserService;
import com.example.projectmanagementsystem.domain.User.auth.UserCredentialsDto;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void shouldRegisterUserCorrectly() throws Exception {
        // given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("email@email.com")
                .password("123")
                .build();

        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // when + then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.containsString("/auth/register/1")))
                .andExpect(jsonPath("$.id").value(userDto.getId().intValue()))
                .andExpect(jsonPath("$.username").value(userDto.getUsername()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }
}
