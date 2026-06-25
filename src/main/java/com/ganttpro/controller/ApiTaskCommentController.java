package com.ganttpro.controller;

import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskComment;
import com.ganttpro.model.TaskActivityType;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectService;
import com.ganttpro.service.TaskService;
import com.ganttpro.service.TaskCommentService;
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
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/comments")
public class ApiTaskCommentController {
    private final TaskCommentService commentService;
    private final TaskActivityService activityService;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiTaskCommentController(TaskCommentService commentService, TaskActivityService activityService,
                                    TaskService taskService, ProjectService projectService, UserRepository userRepository) {
        this.commentService = commentService;
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

        List<Map<String, Object>> comments = commentService.getComments(task.get()).stream()
                .map(this::commentToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@PathVariable Long projectId,
                                                      @PathVariable Long taskId,
                                                      @RequestBody Map<String, String> body,
                                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return ResponseEntity.status(404).build();

        String text = body.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskComment comment = commentService.addComment(task.get(), user, text);
        activityService.logActivity(task.get(), user, TaskActivityType.COMMENT_ADDED,
            String.format("%s добавил комментарий", user.getName()));

        return ResponseEntity.ok(commentToMap(comment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long projectId,
                                                      @PathVariable Long taskId,
                                                      @PathVariable Long commentId,
                                                      @RequestBody Map<String, String> body,
                                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return ResponseEntity.status(404).build();

        Optional<TaskComment> comment = commentService.getComment(commentId, task.get());
        if (comment.isEmpty()) return ResponseEntity.status(404).build();

        String text = body.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            TaskComment updated = commentService.updateComment(comment.get(), user, text);
            activityService.logActivity(task.get(), user, TaskActivityType.COMMENT_UPDATED,
                String.format("%s обновил комментарий", user.getName()));
            return ResponseEntity.ok(commentToMap(updated));
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                      @PathVariable Long taskId,
                                      @PathVariable Long commentId,
                                      Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return ResponseEntity.status(404).build();

        Optional<TaskComment> comment = commentService.getComment(commentId, task.get());
        if (comment.isEmpty()) return ResponseEntity.status(404).build();

        try {
            commentService.deleteComment(comment.get(), user, project.get());
            activityService.logActivity(task.get(), user, TaskActivityType.COMMENT_DELETED,
                String.format("%s удалил комментарий", user.getName()));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    private Map<String, Object> commentToMap(TaskComment comment) {
        return Map.ofEntries(
            Map.entry("id", comment.getId()),
            Map.entry("text", comment.getText()),
            Map.entry("authorName", comment.getAuthor().getName()),
            Map.entry("authorEmail", comment.getAuthor().getEmail()),
            Map.entry("createdAt", comment.getCreatedAt()),
            Map.entry("updatedAt", comment.getUpdatedAt())
        );
    }
}
