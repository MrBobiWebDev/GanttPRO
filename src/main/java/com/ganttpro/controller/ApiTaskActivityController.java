package com.ganttpro.controller;

import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskActivity;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectService;
import com.ganttpro.service.TaskService;
import com.ganttpro.service.TaskActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/activities")
public class ApiTaskActivityController {
    private final TaskActivityService activityService;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiTaskActivityController(TaskActivityService activityService, TaskService taskService,
                                     ProjectService projectService, UserRepository userRepository) {
        this.activityService = activityService;
        this.taskService = taskService;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    private Optional<Project> getProjectForUser(@PathVariable Long projectId, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return Optional.empty();
        return projectService.getProject(projectId, user);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list(@PathVariable Long projectId,
                                                           @PathVariable Long taskId,
                                                           Authentication authentication) {
        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return ResponseEntity.status(404).build();

        List<Map<String, Object>> activities = activityService.getActivities(task.get()).stream()
                .map(this::activityToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activities);
    }

    private Map<String, Object> activityToMap(TaskActivity activity) {
        return Map.ofEntries(
            Map.entry("id", activity.getId()),
            Map.entry("actionType", activity.getActionType().name()),
            Map.entry("actionDisplay", activity.getActionType().getDisplayName()),
            Map.entry("description", activity.getDescription()),
            Map.entry("actorName", activity.getActor().getName()),
            Map.entry("createdAt", activity.getCreatedAt())
        );
    }
}
