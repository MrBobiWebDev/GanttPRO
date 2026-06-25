package com.ganttpro.controller;

import com.ganttpro.dto.CriticalPathResult;
import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.CriticalPathService;
import com.ganttpro.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects/{projectId}/critical-path")
public class ApiCriticalPathController {
    private final CriticalPathService criticalPathService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiCriticalPathController(CriticalPathService criticalPathService,
                                   ProjectService projectService,
                                   UserRepository userRepository) {
        this.criticalPathService = criticalPathService;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCriticalPath(@PathVariable Long projectId,
                                                               Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(projectId, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        CriticalPathResult result = criticalPathService.calculateCriticalPath(project.get());

        return ResponseEntity.ok(Map.of(
                "taskIds", result.getTaskIds(),
                "totalDurationDays", result.getTotalDurationDays(),
                "hasCycle", result.getHasCycle(),
                "message", result.getMessage()
        ));
    }
}
