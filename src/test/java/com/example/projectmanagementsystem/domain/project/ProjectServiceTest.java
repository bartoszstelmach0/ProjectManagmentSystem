package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;
    @Mock
    ProjectDtoMapper mapper;

    @Mock
    UserRepository userRepository;
    private ProjectService projectService;

    @BeforeEach
    public void init (){
        MockitoAnnotations.openMocks(this);
        projectService = new ProjectService(projectRepository,mapper, userRepository);
    }

    @Test
    void shouldReturnAllProjects(){
        //given
        Project project = new Project();
        ProjectDTO projectDTO = new ProjectDTO();

        Mockito.when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        Mockito.when(mapper.map(project)).thenReturn(projectDTO);
        //when
        List<ProjectDTO> result = projectService.getAllProjects();
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1,result.size());
        assertEquals(projectDTO,result.get(0));

        Mockito.verify(projectRepository).findAll();
        Mockito.verify(mapper).map(project);
    }

    @Test
    void shouldReturnAllEmptyProjects(){
        //given
        Mockito.when(projectRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<ProjectDTO> result = projectService.getAllProjects();
        //then
        assertNotNull(result);
        assertEquals(0,result.size());
        assertTrue(result.isEmpty());

        Mockito.verify(projectRepository).findAll();
    }

    @Test
    void shouldReturnProjectById(){
        //given
        Project project = new Project();
        ProjectDTO projectDTO = new ProjectDTO();
        Long id = 1L;
        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        Mockito.when(mapper.map(project)).thenReturn(projectDTO);
        //when
        Optional<ProjectDTO> result = projectService.getProjectById(id);
        //then
        assertTrue(result.isPresent());
        assertEquals(projectDTO,result.get());

        Mockito.verify(projectRepository).findById(id);
        Mockito.verify(mapper).map(project);
    }

    @Test
    void shouldReturnEmptyProjectWhenProjectByIdNotExists(){
        //given
        Long id = 1L;
        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.empty());
        //when
        Optional<ProjectDTO> result = projectService.getProjectById(id);
        //then
        assertTrue(result.isEmpty());
        Mockito.verify(projectRepository).findById(id);
    }

    @Test
    void shouldThrowException(){
        //given
        Long id = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectById(id));
        assertEquals("Invalid project ID " + id , exception.getMessage());
    }

    @Test
    void shouldCreateNewProject(){
        //given
        ProjectDTO projectDTO = new ProjectDTO();
        Project project = new Project();
        Project savedProject = new Project();
        ProjectDTO savedProjectDto = new ProjectDTO();
        User user = new User();

        Mockito.when(userRepository.findById(projectDTO.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(mapper.map(projectDTO)).thenReturn(project);
        Mockito.when(projectRepository.save(project)).thenReturn(savedProject);
        Mockito.when(mapper.map(savedProject)).thenReturn(savedProjectDto);
        //when
        ProjectDTO result = projectService.createNewProject(projectDTO);
        //then
        assertNotNull(result);
        assertEquals(savedProjectDto,result);

        Mockito.verify(mapper).map(projectDTO);
        Mockito.verify(projectRepository).save(project);
        Mockito.verify(mapper).map(savedProject);
    }

    @Test
    void shouldThrowExceptionWhenCreatingProjectWithNull(){
        //given
        ProjectDTO projectDTO = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.createNewProject(projectDTO));
        assertEquals("ProjectDto cannot be null!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidData(){
        //given
        Long id = null;
        ProjectDTO projectDTO = null;

        //when +then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> projectService.updateProject(id,projectDTO));
        assertEquals("Invalid parameters!",exception.getMessage());
    }

    @Test
    void shouldCorrectlyUpdateProject(){
        //given
        Long id = 1L;
        Project existingProject = new Project();
        ProjectDTO projectDTO = new ProjectDTO();
        Project updatedProject = new Project();
        ProjectDTO updatedProjectDTO = new ProjectDTO();
        User user = new User();

        Mockito.when(userRepository.findById(projectDTO.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.of(existingProject));
        Mockito.when(projectRepository.save(existingProject)).thenReturn(updatedProject);
        Mockito.when(mapper.map(updatedProject)).thenReturn(updatedProjectDTO);
        //when
        Optional<ProjectDTO> result = projectService.updateProject(id, projectDTO);
        //then
        assertTrue(result.isPresent());
        assertEquals(updatedProjectDTO,result.get());

        Mockito.verify(projectRepository).findById(id);
        Mockito.verify(projectRepository).save(existingProject);
        Mockito.verify(mapper).map(updatedProject);
    }

    @Test
    void shouldReturnEmptyWhenProjectToUpdateNotFound(){
        //given
        Long id = 1L;
        ProjectDTO projectDTO = new ProjectDTO();

        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.empty());
        //when
        Optional<ProjectDTO> result = projectService.updateProject(id, projectDTO);
        //then
        assertFalse(result.isPresent());

        Mockito.verify(projectRepository).findById(id);
        Mockito.verify(mapper, Mockito.never()).map(any(Project.class));
        Mockito.verify(mapper, Mockito.never()).map((ProjectDTO) any());
        Mockito.verify(projectRepository, Mockito.never()).save(any(Project.class));
    }

    @Test
    void shouldDeleteCorrectly(){
        //given
        Long id = 1L;
        //when
        projectService.deleteProject(id);
        //then
        Mockito.verify(projectRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhileTryingToDelete(){
        //given
        Long id = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.deleteProject(id));
        assertEquals("Argument is not valid!" , exception.getMessage());
    }

    @Test
    void shouldGetProjectByNameContaining (){
        //given
        String name = "example";
        Project project = new Project();
        ProjectDTO projectDTO = new ProjectDTO();
        Mockito.when(projectRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.singletonList(project));
        Mockito.when(mapper.map(project)).thenReturn(projectDTO);
        //when
        List<ProjectDTO> result = projectService.getProjectsByNameContaining(name);
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(projectDTO,result.get(0));

        Mockito.verify(projectRepository).findByNameContainingIgnoreCase(name);
        Mockito.verify(mapper).map(project);
    }

    @Test
    void shouldReturnEmptyWhenNameIsEmpty(){
        //given
        String name = null;
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectsByNameContaining(name));
        assertEquals("Name cannot be empty!", exception.getMessage());
    }

    @Test
    void shouldReturnEmptyListNoProjectsFound(){
        //given
        String name = "No existing";
        Mockito.when(projectRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());
        //when
        List<ProjectDTO> result = projectService.getProjectsByNameContaining(name);
        //then
        assertTrue(result.isEmpty());
        assertNotNull(result);

        Mockito.verify(projectRepository).findByNameContainingIgnoreCase(name);
        Mockito.verify(mapper,Mockito.never()).map(any(Project.class));
    }

    @Test
    void shouldReturnProjectByStartDateBetween(){
        //given
        LocalDateTime startDate = LocalDateTime.of(2023,1,1,1,1);
        LocalDateTime endDate = LocalDateTime.of(2023,12,31,12,12);
        ProjectDTO projectDTO = new ProjectDTO();
        Project project = new Project();

        Mockito.when(projectRepository.findByStartDateBetween(startDate,endDate)).thenReturn(Collections.singletonList(project));
        Mockito.when(mapper.map(project)).thenReturn(projectDTO);
        //when
        List<ProjectDTO> result = projectService.getProjectByStartDateBetween(startDate, endDate);
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(projectDTO,result.get(0));

        Mockito.verify(projectRepository).findByStartDateBetween(startDate,endDate);
        Mockito.verify(mapper).map(project);
    }

    @Test
    void shouldReturnExceptionWithDates(){
        //given
        LocalDateTime startDate = LocalDateTime.of(2022,1,1,1,1);
        LocalDateTime endDate = LocalDateTime.of(2021,1,1,1,1);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectByStartDateBetween(startDate, endDate));
        assertEquals("Start date cannot be after end date",exception.getMessage());

        Mockito.verify(projectRepository,Mockito.never()).findByStartDateBetween(any(),any());
    }

    @Test
    void shouldReturnEmptyList(){
        //given
        LocalDateTime startDate = LocalDateTime.of(2021,1,1,1,1);
        LocalDateTime endDate = LocalDateTime.of(2022,1,1,1,1);
        Mockito.when(projectRepository.findByStartDateBetween(startDate,endDate)).thenReturn(Collections.emptyList());
        //when
        List<ProjectDTO> result = projectService.getProjectByStartDateBetween(startDate, endDate);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(projectRepository).findByStartDateBetween(startDate,endDate);
        Mockito.verify(mapper,Mockito.never()).map(any(Project.class));
    }


    @Test
    void shouldReturnProjectByEndDateBetween(){
        //given
        LocalDateTime startDate = LocalDateTime.of(2023,1,1,1,1);
        LocalDateTime endDate = LocalDateTime.of(2023,12,31,12,12);
        ProjectDTO projectDTO = new ProjectDTO();
        Project project = new Project();

        Mockito.when(projectRepository.findByEndDateBetween(startDate,endDate)).thenReturn(Collections.singletonList(project));
        Mockito.when(mapper.map(project)).thenReturn(projectDTO);
        //when
        List<ProjectDTO> result = projectService.getProjectByEndDateBetween(startDate, endDate);
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(projectDTO,result.get(0));

        Mockito.verify(projectRepository).findByEndDateBetween(startDate,endDate);
        Mockito.verify(mapper).map(project);
    }

    @Test
    void shouldReturnExceptionWithEndingDates(){
        //given
        LocalDateTime startDate = LocalDateTime.of(2022,1,1,1,1);
        LocalDateTime endDate = LocalDateTime.of(2021,1,1,1,1);
        //when + then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projectService.getProjectByEndDateBetween(startDate, endDate));
        assertEquals("Start date cannot be after end date",exception.getMessage());

        Mockito.verify(projectRepository,Mockito.never()).findByEndDateBetween(any(),any());
    }

    @Test
    void shouldReturnEmptyListWithEndDate(){
        //given
        LocalDateTime startDate = LocalDateTime.of(2021,1,1,1,1);
        LocalDateTime endDate = LocalDateTime.of(2022,1,1,1,1);
        Mockito.when(projectRepository.findByEndDateBetween(startDate,endDate)).thenReturn(Collections.emptyList());
        //when
        List<ProjectDTO> result = projectService.getProjectByEndDateBetween(startDate, endDate);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(projectRepository).findByEndDateBetween(startDate,endDate);
        Mockito.verify(mapper,Mockito.never()).map(any(Project.class));
    }

}