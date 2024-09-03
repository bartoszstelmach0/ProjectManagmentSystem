package com.example.projectmanagementsystem.domain.User;

import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.Role.RoleRepository;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    UserDtoMapper mapper;
    @Mock
    PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    public void init (){
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository,roleRepository,mapper, passwordEncoder);
    }

    @Test
    public void shouldReturnAllUsers(){
        //given
        User user = new User();
        UserDto dto = new UserDto();

        Mockito.when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        Mockito.when(mapper.map(user)).thenReturn(dto);
        //when
        List<UserDto> result = userService.getAllUsers();
        //then
        assertNotNull(result);
        assertEquals(dto, result.get(0));
        assertEquals(1,result.size());

        Mockito.verify(userRepository).findAll();
        Mockito.verify(mapper).map(user);
    }

    @Test
    public void shouldReturnEmptyList() {
        //given
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<UserDto> result = userService.getAllUsers();
        //then
        assertNotNull(result);
        assertEquals(0, result.size());

        Mockito.verify(userRepository).findAll();
        Mockito.verify(mapper, Mockito.never()).map(Mockito.any(User.class));
    }

    @Test
    public void shouldFindUserByIdCorrectly(){
        //given
        Long userId = 1L;
        UserDto dto = new UserDto();
        User user = new User();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(mapper.map(user)).thenReturn(dto);
        //when
        Optional<UserDto> result = userService.findUserById(userId);

        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(dto,result.get());

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(mapper).map(user);
    }

    @Test
    public void shouldCreateUserCorrectly(){
        //given
        UserDto givenDto = new UserDto();
        User userToSave = new User();
        User savedUser = new User();
        Role foundRole = new Role();
        UserDto savedUserDto = new UserDto();

        Mockito.when(mapper.map(givenDto)).thenReturn(userToSave);
        Mockito.when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(foundRole));
        Mockito.when(userRepository.save(userToSave)).thenReturn(savedUser);
        Mockito.when(mapper.map(savedUser)).thenReturn(savedUserDto);
        //when
        UserDto result = userService.createUser(givenDto);
        //then
        assertNotNull(result);
        assertEquals(result,savedUserDto);

        Mockito.verify(mapper).map(givenDto);
        Mockito.verify(userRepository).save(userToSave);
        Mockito.verify(mapper).map(savedUser);
    }

    @Test
    public void shouldThrowExceptionWhenUserDtoIsNull(){
        //given
        UserDto dto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        assertEquals("UserDto cannot be null!",exception.getMessage());
    }

    @Test
    public void shouldUpdateCorrectly(){
        //given
        Long id = 1L;
        Role role = new Role();
        UserDto givenDto = new UserDto();
        givenDto.setUsername("newUsername");
        givenDto.setRoles(Set.of("ROLE_USER"));
        User existingUser = new User();
        User savedUser = new User();
        UserDto updatedUser = new UserDto();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        Mockito.when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        Mockito.when(userRepository.save(existingUser)).thenReturn(savedUser);
        Mockito.when(mapper.map(savedUser)).thenReturn(updatedUser);

        //when
        Optional<UserDto> result = userService.updateUser(id, givenDto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(updatedUser,result.get());

        assertEquals("newUsername", existingUser.getUsername());
        assertTrue(existingUser.getRoles().contains(role));

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(roleRepository).findByName(Role.RoleName.ROLE_USER);
        Mockito.verify(userRepository).save(existingUser);
        Mockito.verify(mapper).map(savedUser);
    }

    @Test
    public void shouldReturnEmptyWhenProjectToUpdateNotFound(){
        //given
        Long id = 1L;
        UserDto userDto = new UserDto();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
        //when
        Optional<UserDto> result = userService.updateUser(id, userDto);

        //then
        assertFalse(result.isPresent());

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(mapper,Mockito.never()).map(Mockito.any(User.class));
        Mockito.verify(mapper,Mockito.never()).map(Mockito.any(UserDto.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidData() {
        Long id = null;
        UserDto userDto = null;

        //when +then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> userService.updateUser(id,userDto));
        assertEquals("Invalid parameters!",exception.getMessage());
    }

    @Test
    void shouldDeleteCorrectly(){
        //given
        Long id = 1L;
        //when
        userService.deleteUser(id);
        //then
        Mockito.verify(userRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhileTryingToDelete(){
        //given
        Long id = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(id));
        assertEquals("Argument is not valid!" , exception.getMessage());
    }
}