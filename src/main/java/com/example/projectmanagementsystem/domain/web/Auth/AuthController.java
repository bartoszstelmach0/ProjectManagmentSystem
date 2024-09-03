package com.example.projectmanagementsystem.domain.web.Auth;

import com.example.projectmanagementsystem.domain.User.UserService;
import com.example.projectmanagementsystem.domain.User.auth.UserCredentialsDto;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser (@RequestBody UserDto userDto){
        UserDto savedUser = userService.createUser(userDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(uri).body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody UserCredentialsDto credentials){
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.email(),
                        credentials.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return ResponseEntity.ok("User authenticated successfully");
    }
}
