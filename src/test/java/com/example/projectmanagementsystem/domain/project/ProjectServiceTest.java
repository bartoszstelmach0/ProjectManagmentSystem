package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;
    @Mock
    ProjectDtoMapper mapper;

    private ProjectService projectService;

    @BeforeEach
    public void init (){
        MockitoAnnotations.openMocks(this);
        projectService = new ProjectService(projectRepository,mapper);
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
}