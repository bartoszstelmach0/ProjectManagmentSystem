package com.example.projectmanagementsystem.domain.Role;

import com.example.projectmanagementsystem.domain.Role.dto.RoleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleMapper roleMapper;
    private RoleService roleService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        roleService = new RoleService(roleRepository,roleMapper);
    }

    @Test
    void shouldGetAllRoles(){
        //given
        Role role = new Role();
        RoleDto dto = new RoleDto();
        Mockito.when(roleRepository.findAll()).thenReturn(Collections.singletonList(role));
        Mockito.when(roleMapper.map(role)).thenReturn(dto);
        //when
        List<RoleDto> result = roleService.getAllRoles();
        //then
        assertNotNull(result);
        assertEquals(dto,result.get(0));
        assertEquals(1,result.size());

        Mockito.verify(roleRepository).findAll();
        Mockito.verify(roleMapper).map(role);
    }

    @Test
    void shouldReturnEmptyList(){
        //given
        Mockito.when(roleRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<RoleDto> result = roleService.getAllRoles();
        //then
        assertNotNull(result);
        assertEquals(0,result.size());

        Mockito.verify(roleRepository).findAll();
    }

    @Test
    void shouldReturnRoleById(){
        //given
        Long id = 1L;
        Role role = new Role();
        RoleDto dto = new RoleDto();

        Mockito.when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        Mockito.when(roleMapper.map(role)).thenReturn(dto);
        //when
        Optional<RoleDto> result = roleService.getRoleById(id);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(dto,result.get());

        Mockito.verify(roleRepository).findById(id);
        Mockito.verify(roleMapper).map(role);
    }


    @Test
    void shouldThrowExceptionWithInvalidId(){
        //given
        Long id = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.getRoleById(id));
        assertEquals("Argument is not valid!",exception.getMessage());
    }

}