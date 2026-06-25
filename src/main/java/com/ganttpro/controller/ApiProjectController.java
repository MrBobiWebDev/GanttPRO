package com.ganttpro.controller;

import com.ganttpro.dto.ProjectForm;
import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ApiProjectController {
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiProjectController(ProjectService projectService, UserRepository userRepository) {
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        List<Map<String, Object>> projects = projectService.getUserProjects(user).stream()
                .map(this::projectToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProject(@PathVariable Long id,
                                                          Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(id, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        return ResponseEntity.ok(projectToMap(project.get()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody ProjectForm form,
                                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Project project = projectService.createProject(form, user);
        return ResponseEntity.ok(projectToMap(project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                      @RequestBody ProjectForm form,
                                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(id, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        try {
            Project updated = projectService.updateProject(project.get(), form, user);
            return ResponseEntity.ok(projectToMap(updated));
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(id, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        try {
            projectService.deleteProject(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    private Map<String, Object> projectToMap(Project project) {
        return Map.of(
                "id", project.getId(),
                "name", project.getName(),
                "description", project.getDescription() != null ? project.getDescription() : "",
                "startDate", project.getStartDate(),
                "endDate", project.getEndDate(),
                "taskCount", project.getTasks().size(),
                "activeTasks", project.getTasks().stream()
                        .filter(t -> !t.getStatus().name().equals("DONE"))
                        .count(),
                "createdAt", project.getCreatedAt()
        );
    }
}
