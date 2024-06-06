package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectDtoMapper mapper;

    public ProjectService(ProjectRepository projectRepository, ProjectDtoMapper mapper) {
        this.projectRepository = projectRepository;
        this.mapper = mapper;
    }

    public List<ProjectDTO> getAllProjects(){
        return projectRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
    }

    public Optional<ProjectDTO> getProjectById(Long id){
        return projectRepository.findById(id).map(mapper::map);
    }

    public ProjectDTO createNewProject(ProjectDTO projectDTO){
        Project project = mapper.map(projectDTO);
        Project savedProject = projectRepository.save(project);
        return mapper.map(savedProject);
    }

    public Optional<ProjectDTO> updateProject (Long id, ProjectDTO projectDTO){
       return projectRepository.findById(id)
               .map(existingProject -> {
                   Project project = mapper.map(projectDTO);
                   project.setId(existingProject.getId());
                   Project updatedProject = projectRepository.save(project);
                   return mapper.map(updatedProject);
               });
    }

    public void deleteProject (Long id){
        projectRepository.deleteById(id);
    }

    public List<ProjectDTO> getProjectsByNameContaining(String name){
        return projectRepository.findByNameContainingIgnoreCase(name)
                .stream().map(mapper::map).collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectByStartDateBetween (LocalDateTime startDate, LocalDateTime endDate){
        return  projectRepository.findByStartDateBetween(startDate,endDate).stream().map(mapper::map).collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectByEndDateBetween (LocalDateTime startDate, LocalDateTime endDate){
        return projectRepository.findByEndDateBetween(startDate,endDate).stream().map(mapper::map).collect(Collectors.toList());
    }
}
