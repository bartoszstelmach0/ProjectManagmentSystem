package com.example.projectmanagementsystem.domain.Role;


import com.example.projectmanagementsystem.domain.Role.dto.RoleDto;
import org.springframework.stereotype.Service;

@Service
public class RoleMapper {

    public RoleDto map (Role role){
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName().name())
                .build();
    }

    public Role map (RoleDto dto){
        return Role.builder()
                .id(dto.getId())
                .name(Role.RoleName.valueOf(dto.getName()))
                .build();
    }
}
