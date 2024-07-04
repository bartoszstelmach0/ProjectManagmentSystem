package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectDtoMapper mapper;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectDtoMapper mapper, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    public List<ProjectDTO> getAllProjects(){
        return projectRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
    }

    public Optional<ProjectDTO> getProjectById(Long id){
        if (id == null || id <= 0){
            throw new IllegalArgumentException("Invalid project ID " + id);
        }
        return projectRepository
                .findById(id)
                .map(mapper::map);
    }

    public ProjectDTO createNewProject(ProjectDTO projectDTO){
        if (projectDTO == null){
            throw  new IllegalArgumentException("ProjectDto cannot be null!");
        }
        User user = userRepository.findById(projectDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID " + projectDTO.getUserId()));

        Project project = mapper.map(projectDTO);
        project.setUser(user);
        Project savedProject = projectRepository.save(project);
        return mapper.map(savedProject);
    }

    public Optional<ProjectDTO> updateProject (Long id, ProjectDTO projectDTO){
        if (id == null || id < 0 || projectDTO == null){
            throw new IllegalArgumentException("Invalid parameters!");
        }
       return projectRepository.findById(id)
               .map(existingProject -> {
                   if (projectDTO.getName() != null) {
                       existingProject.setName(projectDTO.getName());
                   }
                   if (projectDTO.getDescription() != null) {
                       existingProject.setDescription(projectDTO.getDescription());
                   }
                   if (projectDTO.getStartDate() != null) {
                       existingProject.setStartDate(projectDTO.getStartDate());
                   }
                   if (projectDTO.getEndDate() != null) {
                       existingProject.setEndDate(projectDTO.getEndDate());
                   }
                   existingProject.setUser(userRepository.findById(projectDTO.getUserId())
                           .orElseThrow(() -> new RuntimeException("User not found with id " +projectDTO.getUserId())));
                   Project updatedProject = projectRepository.save(existingProject);
                   return mapper.map(updatedProject);
               });
    }

    public void deleteProject (Long id){
        if (id == null || id < 0){
            throw new IllegalArgumentException("Argument is not valid!");
        }
        projectRepository.deleteById(id);
    }

    public List<ProjectDTO> getProjectsByNameContaining(String name){
        if(name == null){
            throw new IllegalArgumentException("Name cannot be empty!");
        }
        return projectRepository.findByNameContainingIgnoreCase(name)
                .stream().map(mapper::map).collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectByStartDateBetween (LocalDateTime startDate, LocalDateTime endDate){
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return  projectRepository.findByStartDateBetween(startDate,endDate).stream().map(mapper::map).collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectByEndDateBetween (LocalDateTime startDate, LocalDateTime endDate){
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return projectRepository.findByEndDateBetween(startDate,endDate).stream().map(mapper::map).collect(Collectors.toList());
    }
}
