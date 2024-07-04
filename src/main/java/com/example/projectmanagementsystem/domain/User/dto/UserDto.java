package com.example.projectmanagementsystem.domain.User.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    Long id;
    @NotBlank
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
    @NotEmpty
    private Set<String> roles;
    private List<String> projectNames = new ArrayList<>();
    private List<String> taskNames = new ArrayList<>();

}
