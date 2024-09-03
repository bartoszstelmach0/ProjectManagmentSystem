package com.example.projectmanagementsystem.domain.Role;

import com.example.projectmanagementsystem.domain.Role.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleServiceTestIT {


    @Autowired
    private  RoleService roleService;
    @Autowired

    private  RoleRepository roleRepository;

    @Autowired
    private  RoleMapper roleMapper;

    @Test
    void shouldGetAllRoles() {
        // given
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);
        roleRepository.save(role);

        // when
        List<RoleDto> result = roleService.getAllRoles();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ROLE_USER", result.get(0).getName());
    }

    @Test
    void shouldGetRoleById(){
        //given
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);
        roleRepository.save(role);
        Long roleId = role.getId();
        //when
        Optional<RoleDto> result = roleService.getRoleById(roleId);
        //then
        assertTrue(result.isPresent());
        assertEquals("ROLE_USER",result.get().getName());
    }

    @Test
    void shouldThrowExceptionWithInvalidId(){
        //given
        Long id = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.getRoleById(id));
        assertEquals(exception.getMessage(),"Argument is not valid!");
    }
}