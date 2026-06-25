package com.ganttpro.controller;

import com.ganttpro.dto.PlanningIssue;
import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.PlanningIssueService;
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
@RequestMapping("/api/projects/{projectId}/planning-issues")
public class ApiPlanningIssueController {
    private final PlanningIssueService planningIssueService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiPlanningIssueController(PlanningIssueService planningIssueService,
                                    ProjectService projectService,
                                    UserRepository userRepository) {
        this.planningIssueService = planningIssueService;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProjectIssues(@PathVariable Long projectId,
                                                                 Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(projectId, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        List<PlanningIssue> issues = planningIssueService.getProjectIssues(project.get());

        Map<String, Object> response = Map.of(
                "issues", issues.stream().map(this::issueToMap).collect(Collectors.toList()),
                "criticalCount", planningIssueService.getCriticalIssueCount(project.get()),
                "warningCount", planningIssueService.getWarningIssueCount(project.get()),
                "totalCount", issues.size()
        );

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> issueToMap(PlanningIssue issue) {
        return Map.of(
                "type", issue.getType().name(),
                "typeDisplay", issue.getType().getDisplayName(),
                "taskId", issue.getTaskId(),
                "taskTitle", issue.getTaskTitle(),
                "severity", issue.getSeverity().name(),
                "severityDisplay", issue.getSeverity().getDisplayName(),
                "message", issue.getMessage()
        );
    }
}
