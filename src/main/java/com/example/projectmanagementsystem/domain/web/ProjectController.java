package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.project.ProjectService;
import com.example.projectmanagementsystem.domain.project.dto.ProjectDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ObjectMapper objectMapper;

    public ProjectController(ProjectService projectService, ObjectMapper objectMapper) {
        this.projectService = projectService;
        this.objectMapper = objectMapper;
    }


    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects(){
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById (@PathVariable Long id){
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject (@RequestBody @Valid ProjectDTO projectDTO){
        ProjectDTO savedProject = projectService.createNewProject(projectDTO);
        URI savedProjectUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedProject.getId())
                .toUri();
        return ResponseEntity.created(savedProjectUri).body(savedProject);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProject (@PathVariable Long id, @RequestBody JsonMergePatch patch){
        try{
            ProjectDTO projectDTO = projectService.getProjectById(id).orElseThrow(() -> new EntityNotFoundException("Project not found!"));
            ProjectDTO projectPatched = applyPatch(projectDTO,patch);
            projectService.updateProject(id,projectPatched);
        } catch (JsonPatchException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid patch");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing Error");
        }
        return ResponseEntity.noContent().build();
    }

    private ProjectDTO applyPatch(ProjectDTO projectDTO, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode jsonNode = objectMapper.valueToTree(projectDTO);
        JsonNode projectPatchNode = patch.apply(jsonNode);
        return objectMapper.treeToValue(projectPatchNode,ProjectDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name")
    public ResponseEntity<List<ProjectDTO>> getProjectByNameContaining (@RequestParam String name){
        try{
            List<ProjectDTO> projectsByNameContaining = projectService.getProjectsByNameContaining(name);
            return ResponseEntity.ok(projectsByNameContaining);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/startDate")
    public ResponseEntity<List<ProjectDTO>> getProjectByStartDateBetween (@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate){
        try{
            List<ProjectDTO> result = projectService.getProjectByStartDateBetween(startDate, endDate);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/endDate")
    public ResponseEntity<List<ProjectDTO>> getProjectByEndDateBetween (@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate){
        try{
            List<ProjectDTO> result = projectService.getProjectByEndDateBetween(startDate, endDate);
            return ResponseEntity.ok(result);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
