package com.example.projectmanagementsystem.domain.config;

import com.example.projectmanagementsystem.domain.User.UserService;
import com.example.projectmanagementsystem.domain.User.auth.UserCredentialsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(userService);
    }

    @Test
    void shouldLoadUserByUsernameBeCorrect(){
        //given
        String email = "example@example.com";
        UserCredentialsDto userCredentialsDto =
                new UserCredentialsDto(email, "password", Set.of("ROLE_USER"));
        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.of(userCredentialsDto));
        //when
        UserDetails result = customUserDetailsService.loadUserByUsername(email);
        //then
        assertNotNull(result);
        assertEquals(email,userCredentialsDto.email());
        assertEquals(userCredentialsDto.password(),result.getPassword());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        String email = "nonexistent@example.com";
        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.empty());

        // when + then
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                customUserDetailsService.loadUserByUsername(email));
        assertEquals("User does not exist: " + email, exception.getMessage());
    }
}