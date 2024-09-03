package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectServiceTestIT {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectDtoMapper projectDtoMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectService projectService;

    private User defaultUser;
    private Project defaultProject;

    @BeforeEach
    public void init(){
        defaultUser = new User();
        defaultUser.setUsername("User");
        defaultUser.setEmail("example@example.com");
        defaultUser.setPassword("1222");
        userRepository.save(defaultUser);

        defaultProject = new Project();
        defaultProject.setName("Test Project");
        defaultProject.setDescription("Test Project Description");
        defaultProject.setStartDate(LocalDateTime.now().plusHours(1));
        defaultProject.setEndDate(LocalDateTime.now().plusDays(1));
        defaultProject.setUser(defaultUser);
        projectRepository.save(defaultProject);

    }

    @Test
    void shouldGetAllProjects(){
        //given
        projectRepository.save(defaultProject);
        //when
        List<ProjectDTO> result = projectService.getAllProjects();
        //then
        assertNotNull(result);
        assertEquals(1,result.size());

        ProjectDTO projectDTO = projectDtoMapper.map(defaultProject);
        assertEquals(projectDTO.getName(),result.get(0).getName());
    }

    @Test
    void shouldGetProjectByIdCorrectly(){
        //given

        Long projectId = defaultProject.getId();
        //when
        Optional<ProjectDTO> result = projectService.getProjectById(projectId);
        //then
        assertTrue(result.isPresent());
        assertEquals(defaultProject.getName(),result.get().getName());
    }

    @Test
    void shouldThrowExceptionWhenIdEqualsNull(){
        //given
        Long projectId = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectById(projectId));
        assertEquals("Invalid project ID " + projectId,exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenIdIsNegativeNumber(){
        //given
        Long projectId = -1L;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectById(projectId));
        assertEquals("Invalid project ID " + projectId,exception.getMessage());
    }

    @Test
    void shouldCreateNewProjectCorrectly() {
        // given

        TaskDto taskDto = new TaskDto();
        taskDto.setName("Test Task");
        taskDto.setDeadline(LocalDateTime.now().plusDays(1));
        taskDto.setStatus("TO_DO");
        taskDto.setUserId(defaultUser.getId());
        taskDto.setProjectId(defaultUser.getId());

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Test Project");
        projectDTO.setDescription("Test Project Description");
        projectDTO.setStartDate(LocalDateTime.now().plusHours(1));
        projectDTO.setEndDate(LocalDateTime.now().plusDays(1));
        projectDTO.setUserId(defaultUser.getId());
        projectDTO.setTasks(Collections.singletonList(taskDto));

        // when
        ProjectDTO result = projectService.createNewProject(projectDTO);

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals(1, result.getTasks().size());
        assertEquals("Test Task", result.getTasks().get(0).getName());
    }

    @Test
    void shouldThrowExceptionWhenProjectDtoIsNull(){
        //given
        ProjectDTO projectDTO = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.createNewProject(projectDTO));
        assertEquals("ProjectDto cannot be null!",exception.getMessage());
    }

    @Test
    void shouldUpdateProjectCorrectly(){
        //given
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("New Project Name");
        projectDTO.setDescription("New Project Description");
        projectDTO.setStartDate(LocalDateTime.now().plusDays(1));
        projectDTO.setEndDate(LocalDateTime.now().plusDays(2));
        projectDTO.setUserId(defaultUser.getId());
        //when
        Optional<ProjectDTO> result = projectService.updateProject(defaultProject.getId(), projectDTO);
        //then
        assertTrue(result.isPresent());
        assertEquals(projectDTO.getName(),result.get().getName());
        assertEquals(projectDTO.getDescription(),result.get().getDescription());
        assertEquals(projectDTO.getStartDate(),result.get().getStartDate());
        assertEquals(projectDTO.getEndDate(),result.get().getEndDate());
        assertEquals(projectDTO.getUserId(),result.get().getUserId());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNullInUpdatingProject(){
        //given
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("New Project Name");
        projectDTO.setDescription("New Project Description");
        projectDTO.setStartDate(LocalDateTime.now().plusDays(1));
        projectDTO.setEndDate(LocalDateTime.now().plusDays(2));
        projectDTO.setUserId(defaultUser.getId());

        defaultProject.setId(null);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(defaultProject.getId(), projectDTO));
        assertEquals("Invalid parameters!",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenIdIsNegativeNumberInUpdatingProject(){
        //given
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("New Project Name");
        projectDTO.setDescription("New Project Description");
        projectDTO.setStartDate(LocalDateTime.now().plusDays(1));
        projectDTO.setEndDate(LocalDateTime.now().plusDays(2));
        projectDTO.setUserId(defaultUser.getId());

        defaultProject.setId(-1L);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(defaultProject.getId(), projectDTO));
        assertEquals("Invalid parameters!",exception.getMessage());
    }

    @Test
    void shouldDeleteProjectCorrectly(){
        //given
        defaultProject.setId(1L);
        //when
        projectService.deleteProject(defaultProject.getId());
        //then
        Optional<Project> deletedProject = projectRepository.findById(defaultProject.getId());
        assertFalse(deletedProject.isPresent());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNullInDeletingProject(){
        //given
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("New Project Name");
        projectDTO.setDescription("New Project Description");
        projectDTO.setStartDate(LocalDateTime.now().plusDays(1));
        projectDTO.setEndDate(LocalDateTime.now().plusDays(2));
        projectDTO.setUserId(defaultUser.getId());

        defaultProject.setId(null);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.deleteProject(defaultProject.getId()));
        assertEquals("Argument is not valid!",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenIdIsNegativeNumberInDeletingProject(){
        //given
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("New Project Name");
        projectDTO.setDescription("New Project Description");
        projectDTO.setStartDate(LocalDateTime.now().plusDays(1));
        projectDTO.setEndDate(LocalDateTime.now().plusDays(2));
        projectDTO.setUserId(defaultUser.getId());

        defaultProject.setId(-1L);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.deleteProject(defaultProject.getId()));
        assertEquals("Argument is not valid!",exception.getMessage());
    }

    @Test
    void shouldGetProjectByNameContainingCorrectly(){
        //given
        String searchName = "Test";
        //when
        List<ProjectDTO> result = projectService.getProjectsByNameContaining(searchName);
        //then
        assertNotNull(result);
        assertEquals(defaultProject.getName(),result.get(0).getName());
        assertEquals(defaultProject.getDescription(),result.get(0).getDescription());
        assertEquals(defaultProject.getStartDate(),result.get(0).getStartDate());
        assertEquals(defaultProject.getEndDate(),result.get(0).getEndDate());
        assertEquals(defaultProject.getUser().getId(),result.get(0).getUserId());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull(){
        //given
        defaultProject.setName(null);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectsByNameContaining(defaultProject.getName()));
        assertEquals("Name cannot be empty!",exception.getMessage());
    }

    @Test
    void shouldGetProjectsByStartDateBetweenCorrectly(){
        //given
        LocalDateTime startDate = defaultProject.getStartDate().minusDays(1);
        LocalDateTime endDate = defaultProject.getEndDate().plusDays(1);
        //when
        List<ProjectDTO> result = projectService.getProjectByStartDateBetween(startDate, endDate);
        //then
        assertNotNull(result);
        assertEquals(defaultProject.getName(), result.get(0).getName());
        assertEquals(defaultProject.getDescription(), result.get(0).getDescription());
        assertEquals(defaultProject.getStartDate(), result.get(0).getStartDate());
        assertEquals(defaultProject.getEndDate(), result.get(0).getEndDate());
        assertEquals(defaultProject.getUser().getId(), result.get(0).getUserId());
    }


    @Test
    void shouldThrowExceptionWhenStartDateIsAfterEndDate(){
        //given
        defaultProject.setStartDate(LocalDateTime.now().plusHours(3));
        defaultProject.setEndDate(LocalDateTime.now().plusHours(1));
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectByStartDateBetween(defaultProject.getStartDate(),defaultProject.getEndDate()));
        assertEquals("Start date cannot be after end date",exception.getMessage());
    }

    @Test
    void shouldGetProjectsByEndDateBetweenCorrectly(){
        //given
        LocalDateTime startDate = defaultProject.getStartDate().minusDays(1);
        LocalDateTime endDate = defaultProject.getEndDate().plusDays(1);
        //when
        List<ProjectDTO> result = projectService.getProjectByEndDateBetween(startDate,endDate );
        //then
        assertNotNull(result);
        assertEquals(defaultProject.getName(),result.get(0).getName());
        assertEquals(defaultProject.getDescription(),result.get(0).getDescription());
        assertEquals(defaultProject.getStartDate(),result.get(0).getStartDate());
        assertEquals(defaultProject.getEndDate(),result.get(0).getEndDate());
        assertEquals(defaultProject.getUser().getId(),result.get(0).getUserId());
    }
}