package com.example.projectmanagementsystem.domain.config;

import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.User.UserService;
import com.example.projectmanagementsystem.domain.User.auth.UserCredentialsDto;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new NoSuchElementException("User does not exist: " + username));
    }


    private UserDetails createUserDetails(UserCredentialsDto credentials){
        String[] roles = credentials.roles().stream()
                .map(role -> role.replace("ROLE_", ""))
                .toArray(String[]::new);

        return User.builder()
                .username(credentials.email())
                .password(credentials.password())
                .roles(roles)
                .build();
    }
}
