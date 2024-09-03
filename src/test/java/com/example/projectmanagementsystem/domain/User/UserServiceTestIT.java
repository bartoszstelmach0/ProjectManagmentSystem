package com.example.projectmanagementsystem.domain.User;

import com.example.projectmanagementsystem.domain.Comment.Comment;
import com.example.projectmanagementsystem.domain.Comment.CommentRepository;
import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.Role.RoleRepository;
import com.example.projectmanagementsystem.domain.User.auth.UserCredentialsDto;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.Task;
import com.example.projectmanagementsystem.domain.task.TaskRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTestIT {


    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserDtoMapper userDtoMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    CommentRepository commentRepository;

    private Role defaultRole;
    private User defaultUser;
    private Project defaultProject;
    private Task defaultTask;
    private Comment defaultComment;
    @BeforeEach
    public void init(){
        defaultRole = Role.builder()
                .name(Role.RoleName.ROLE_USER)
                .build();
        roleRepository.save(defaultRole);

        defaultUser = new User();
        defaultUser.setUsername("User");
        defaultUser.setEmail("example@example.com");
        defaultUser.setPassword(passwordEncoder.encode("122"));
        defaultUser.setRoles(Collections.singleton(defaultRole));
        userRepository.save(defaultUser);

        defaultProject = new Project();
        defaultProject.setName("Test Project");
        defaultProject.setDescription("Test Project Description");
        defaultProject.setStartDate(LocalDateTime.now().plusHours(1));
        defaultProject.setEndDate(LocalDateTime.now().plusDays(1));
        defaultProject.setUser(defaultUser);
        projectRepository.save(defaultProject);

        defaultTask = Task.builder()
                .name("task")
                .status(Task.TaskStatus.TO_DO)
                .deadline(LocalDateTime.now().plusDays(1))
                .project(defaultProject)
                .user(defaultUser)
                .build();
        taskRepository.save(defaultTask);

        defaultComment = Comment.builder()
                .comment("comment")
                .user(defaultUser)
                .project(defaultProject)
                .task(defaultTask)
                .build();
        commentRepository.save(defaultComment);
    }


    @Test
    void shouldGetAllUsersCorrectly(){
        //when
        List<UserDto> result = userService.getAllUsers();
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(result.get(0).getUsername(),defaultUser.getUsername());
        assertEquals(result.get(0).getEmail(),defaultUser.getEmail());
        assertEquals(result.get(0).getPassword(),defaultUser.getPassword());
        assertEquals(result.get(0).getRoles(), Set.of("ROLE_USER"));
    }

    @Test
    void shouldFindUserByIdCorrectly(){
        UserDto userDto = UserDto.builder()
                .username("User")
                .email("example@example.com")
                .password(passwordEncoder.encode("122"))
                .roles(Set.of("ROLE_USER"))
                .build();
        //when
        Optional<UserDto> result = userService.findUserById(defaultUser.getId());
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(),userDto.getUsername());
        assertEquals(result.get().getEmail(),userDto.getEmail());
        assertTrue(passwordEncoder.matches("122",result.get().getPassword()));
        assertEquals(result.get().getRoles(),userDto.getRoles());
    }

    @Test
    void shouldFindByEmailCorrectly(){
        //given
        UserCredentialsDto dto = new UserCredentialsDto(defaultUser.getEmail(),
                defaultUser.getPassword(),
                Set.of(defaultRole.getName().name()));
        //when
        Optional<UserCredentialsDto> result = userService.findByEmail(defaultUser.getEmail());
        //then
        assertNotNull(dto);
        assertTrue(result.isPresent());
        assertEquals(dto.email(), result.get().email());
        assertEquals(dto.password(), result.get().password());
        assertEquals(dto.roles(), result.get().roles());
    }

    @Test
    void shouldCreateUserCorrectly(){

        UserDto userDto = UserDto.builder()
                .username("User")
                .email("example@example.com")
                .password(passwordEncoder.encode("122"))
                .roles(Set.of("ROLE_USER"))
                .projectNames(new ArrayList<>())
                .taskNames(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
        //when
        UserDto user = userService.createUser(userDto);
        //then
        assertNotNull(user);
        assertEquals(user.getUsername(), defaultUser.getUsername());
        assertEquals(user.getEmail(), defaultUser.getEmail());
        assertEquals(user.getRoles(), Set.of("ROLE_USER"));
    }

    @Test
    void shouldThrowExceptionWhenDtoIsNullDurningCreatingUser(){
        //given
        UserDto dto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        assertEquals("UserDto cannot be null!",exception.getMessage());
    }

    @Test
    void shouldUpdateUserCorrectly(){
        //given
        UserDto userDto = UserDto.builder()
                .username("User")
                .email("example@example.com")
                .password(passwordEncoder.encode("122"))
                .roles(Set.of("ROLE_USER"))
                .projectNames(new ArrayList<>())
                .taskNames(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        //when
        Optional<UserDto> result = userService.updateUser(defaultUser.getId(), userDto);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(), defaultUser.getUsername());
        assertEquals(result.get().getEmail(), defaultUser.getEmail());
        assertEquals(result.get().getRoles(), Set.of("ROLE_USER"));
    }
    @Test
    void shouldThrowExceptionWhenDtoIsNullDurningUpdatingUser(){
        //given
        UserDto userDto = UserDto.builder()
                .username("User")
                .email("example@example.com")
                .password(passwordEncoder.encode("122"))
                .roles(Set.of("ROLE_USER"))
                .projectNames(new ArrayList<>())
                .taskNames(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        defaultUser.setId(null);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(defaultUser.getId(),userDto));
        assertEquals("Invalid parameters!",exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDtoIsNegativeNumberDurningUpdatingUser(){
        //given
        UserDto userDto = UserDto.builder()
                .username("User")
                .email("example@example.com")
                .password(passwordEncoder.encode("122"))
                .roles(Set.of("ROLE_USER"))
                .projectNames(new ArrayList<>())
                .taskNames(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        defaultUser.setId(-1L);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(defaultUser.getId(),userDto));
        assertEquals("Invalid parameters!",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenUserDtoIsNullDurningUpdatingUser(){
        //given
        UserDto userDto = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(defaultUser.getId(),userDto));
        assertEquals("Invalid parameters!",exception.getMessage());
    }

    @Test
    void shouldDeleteUserCorrectly(){
        //given
        defaultUser.setId(1L);
        //when
        userService.deleteUser(defaultUser.getId());
        //then
        Optional<User> result = userRepository.findById(defaultUser.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNullDurningDeleting(){
        //given
        defaultUser.setId(null);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(defaultUser.getId()));
        assertEquals("Argument is not valid!",exception.getMessage());
    }

}