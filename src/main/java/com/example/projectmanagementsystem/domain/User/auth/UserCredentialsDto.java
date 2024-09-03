package com.example.projectmanagementsystem.domain.User.auth;

import java.util.Set;

public record UserCredentialsDto(String email, String password, Set<String> roles) {
}