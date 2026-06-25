package com.ganttpro.controller;

import com.ganttpro.dto.TaskForm;
import com.ganttpro.model.*;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectService;
import com.ganttpro.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class ApiTaskController {
    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiTaskController(TaskService taskService, ProjectService projectService,
                            UserRepository userRepository) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    private Optional<Project> getProjectForUser(@PathVariable Long projectId,
                                                 Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return Optional.empty();
        return projectService.getProject(projectId, user);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list(@PathVariable Long projectId,
                                                          Authentication authentication) {
        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        List<Map<String, Object>> tasks = taskService.getProjectTasks(project.get()).stream()
                .map(this::taskToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable Long projectId,
                                                       @PathVariable Long taskId,
                                                       Authentication authentication) {
        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return ResponseEntity.status(404).build();

        return ResponseEntity.ok(taskToMap(task.get()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@PathVariable Long projectId,
                                                      @RequestBody TaskForm form,
                                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        if (!taskService.validateDates(form)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Task task = taskService.createTask(project.get(), form, user);
            return ResponseEntity.ok(taskToMap(task));
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long projectId,
                                                      @PathVariable Long taskId,
                                                      @RequestBody TaskForm form,
                                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return ResponseEntity.status(404).build();

        if (!taskService.validateDates(form)) {
            return ResponseEntity.badRequest().build();
        }

        if (!taskService.validateProgress(form.getProgress())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Task updated = taskService.updateTask(task.get(), form, user);
            return ResponseEntity.ok(taskToMap(updated));
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                      @PathVariable Long taskId,
                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return ResponseEntity.status(404).build();

        try {
            taskService.deleteTask(taskId, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    private Map<String, Object> taskToMap(Task task) {
        boolean isOverdue = task.getEndDate().isBefore(LocalDate.now()) &&
                           !task.getStatus().equals(TaskStatus.DONE);
        boolean hasSubtasks = !task.getSubtasks().isEmpty();
        Map<String, Object> map = new HashMap<>();
        map.put("id", task.getId());
        map.put("title", task.getTitle());
        map.put("description", task.getDescription() != null ? task.getDescription() : "");
        map.put("startDate", task.getStartDate());
        map.put("endDate", task.getEndDate());
        map.put("status", task.getStatus().name());
        map.put("priority", task.getPriority().name());
        map.put("progress", task.getProgress());
        map.put("assigneeName", task.getAssigneeName() != null ? task.getAssigneeName() : "");
        map.put("isOverdue", isOverdue);
        map.put("createdAt", task.getCreatedAt());
        map.put("parentTaskId", task.getParentTask() != null ? task.getParentTask().getId() : null);
        map.put("hasSubtasks", hasSubtasks);
        map.put("subtasks", task.getSubtasks().stream().map(this::taskToMap).collect(Collectors.toList()));
        return map;
    }
}
