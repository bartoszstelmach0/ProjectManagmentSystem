package com.example.projectmanagementsystem.domain.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RoleRepositoryTestIT {

    @Autowired
    RoleRepository roleRepository;
    private Role defaultRole;
    @BeforeEach
    public void init(){
        defaultRole = Role.builder()
                .name(Role.RoleName.ROLE_USER)
                .build();
        roleRepository.save(defaultRole);
    }
    @Test
    void shouldFindByNameCorrectly(){
        //when
        Optional<Role> result = roleRepository.findByName(defaultRole.getName());
        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result.get().getName(),defaultRole.getName());
    }
}