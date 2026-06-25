package com.ganttpro.controller;

import com.ganttpro.dto.AssigneeWorkload;
import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.AssigneeWorkloadService;
import com.ganttpro.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/assignee-workload")
public class ApiAssigneeWorkloadController {
    private final AssigneeWorkloadService assigneeWorkloadService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiAssigneeWorkloadController(AssigneeWorkloadService assigneeWorkloadService,
                                       ProjectService projectService,
                                       UserRepository userRepository) {
        this.assigneeWorkloadService = assigneeWorkloadService;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getProjectWorkload(@PathVariable Long projectId,
                                                                        Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(projectId, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        List<Map<String, Object>> workload = assigneeWorkloadService.getProjectWorkload(project.get())
                .stream()
                .map(this::workloadToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(workload);
    }

    private Map<String, Object> workloadToMap(AssigneeWorkload workload) {
        return Map.of(
                "assigneeName", workload.getAssigneeName(),
                "totalTasks", workload.getTotalTasks(),
                "inProgressTasks", workload.getInProgressTasks(),
                "doneTasks", workload.getDoneTasks(),
                "overdueTasks", workload.getOverdueTasks(),
                "averageProgress", workload.getAverageProgress(),
                "criticalPriorityTasks", workload.getCriticalPriorityTasks(),
                "criticalPathTasks", workload.getCriticalPathTasks()
        );
    }
}
