package com.ganttpro.controller;

import com.ganttpro.model.Project;
import com.ganttpro.model.TaskDependency;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectService;
import com.ganttpro.service.TaskDependencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/dependencies")
public class ApiTaskDependencyController {
    private final TaskDependencyService taskDependencyService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiTaskDependencyController(TaskDependencyService taskDependencyService,
                                     ProjectService projectService,
                                     UserRepository userRepository) {
        this.taskDependencyService = taskDependencyService;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list(@PathVariable Long projectId,
                                                          @PathVariable Long taskId,
                                                          Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(projectId, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        try {
            List<Map<String, Object>> dependencies = taskDependencyService.getTaskDependencies(projectId, taskId).stream()
                    .map(this::dependencyToMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dependencies);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@PathVariable Long projectId,
                                                      @PathVariable Long taskId,
                                                      @RequestBody Map<String, Long> body,
                                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(projectId, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        try {
            Long dependsOnTaskId = body.get("dependsOnTaskId");
            TaskDependency dependency = taskDependencyService.createDependency(projectId, taskId, dependsOnTaskId);
            return ResponseEntity.ok(dependencyToMap(dependency));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{dependencyId}")
    public ResponseEntity<Void> remove(@PathVariable Long projectId,
                                      @PathVariable Long taskId,
                                      @PathVariable Long dependencyId,
                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(projectId, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        try {
            taskDependencyService.removeDependency(projectId, dependencyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    private Map<String, Object> dependencyToMap(TaskDependency dependency) {
        return Map.of(
                "id", dependency.getId(),
                "taskId", dependency.getTask().getId(),
                "dependsOnTaskId", dependency.getDependsOnTask().getId(),
                "dependsOnTaskTitle", dependency.getDependsOnTask().getTitle(),
                "type", dependency.getType().name(),
                "createdAt", dependency.getCreatedAt()
        );
    }
}
