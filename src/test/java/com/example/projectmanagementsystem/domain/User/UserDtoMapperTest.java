package com.example.projectmanagementsystem.domain.User;

import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.Role.RoleRepository;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.project.ProjectRepository;
import com.example.projectmanagementsystem.domain.task.Task;
import com.example.projectmanagementsystem.domain.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoMapperTest {

    @Mock
    RoleRepository roleRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    TaskRepository taskRepository;

    private UserDtoMapper userDtoMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        userDtoMapper = new UserDtoMapper(roleRepository, projectRepository, taskRepository);
    }

    @Test
    void shouldMapToDtoCorrectly() {
        //given
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_ADMIN);

        Project project = new Project();
        project.setName("example project");

        Task task = new Task();
        task.setName("example Task");

        User user = User.builder()
                .id(1L)
                .username("username")
                .email("username@example.com")
                .password("123")
                .roles(Set.of(role))
                .projects(List.of(project))
                .tasks(List.of(task))
                .build();

        //when
        UserDto result = userDtoMapper.map(user);
        //then
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(Set.of("ROLE_ADMIN"), result.getRoles());
        assertEquals(List.of("example project"), result.getProjectNames());
        assertEquals(List.of("example Task"), result.getTaskNames());
    }
    @Test
    void shouldMapToEntityCorrectly(){
        //given
        String role = "ROLE_ADMIN";
        String projectName = "example project";
        String taskNames = "example task";

        Role roleEntity = new Role();
        roleEntity.setName(Role.RoleName.ROLE_ADMIN);

        Project project = new Project();
        project.setName(projectName);

        Task task = new Task();
        task.setName(taskNames);

        UserDto dto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .password("123")
                .roles(Set.of(role))
                .projectNames(List.of(projectName))
                .taskNames(List.of(taskNames))
                .build();

        Mockito.when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.of(roleEntity));
        Mockito.when(projectRepository.findByNameContainingIgnoreCase(projectName)).thenReturn(Collections.singletonList(project));
        Mockito.when(taskRepository.findByName(taskNames)).thenReturn(Optional.of(task));
        //when
        User result = userDtoMapper.map(dto);
        //then
        assertNotNull(result);
        assertEquals(dto.getId(),result.getId());
        assertEquals(dto.getUsername(),result.getUsername());
        assertEquals(dto.getPassword(),result.getPassword());
        assertEquals(result.getRoles(),Set.of(roleEntity));
        assertEquals(result.getProjects(),List.of(project));
        assertEquals(result.getTasks(),List.of(task));


    }

}

