package com.example.projectmanagementsystem.domain.Role;

import com.example.projectmanagementsystem.domain.Role.dto.RoleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {

    private RoleMapper roleMapper;

    @BeforeEach
    public void init (){
        MockitoAnnotations.openMocks(this);
        roleMapper = new RoleMapper();
    }

    @Test
    void shouldMapToDtoCorrectly(){
        //given
        String roleName = "ROLE_ADMIN";
        Role role = Role.builder()
                .id(1L)
                .name(Role.RoleName.ROLE_ADMIN)
                .build();
        //when
        RoleDto result = roleMapper.map(role);
        //then
        assertNotNull(result);
        assertEquals(role.getId(),result.getId());
        assertEquals(roleName, result.getName());
    }

    @Test
    void shouldMapToEntityCorrectly(){
        //given
        RoleDto dto = RoleDto.builder()
                .id(1L)
                .name("ROLE_ADMIN")
                .build();
        //when
        Role result = roleMapper.map(dto);
        //then
        assertNotNull(result);
        assertEquals(dto.getId(),result.getId());
        assertEquals(Role.RoleName.ROLE_ADMIN,result.getName());
    }
}