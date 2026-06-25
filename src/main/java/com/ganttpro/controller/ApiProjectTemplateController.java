package com.ganttpro.controller;

import com.ganttpro.dto.ProjectForm;
import com.ganttpro.model.Project;
import com.ganttpro.model.ProjectTemplate;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/templates")
public class ApiProjectTemplateController {
    private final ProjectTemplateService projectTemplateService;
    private final UserRepository userRepository;

    public ApiProjectTemplateController(ProjectTemplateService projectTemplateService,
                                      UserRepository userRepository) {
        this.projectTemplateService = projectTemplateService;
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

        List<Map<String, Object>> templates = projectTemplateService.getAllTemplates().stream()
                .map(this::templateToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTemplate(@PathVariable Long id,
                                                           Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return projectTemplateService.getTemplate(id)
                .map(t -> ResponseEntity.ok(templateToMap(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/create-project")
    public ResponseEntity<Map<String, Object>> createProject(@PathVariable Long id,
                                                             @RequestBody Map<String, Object> body,
                                                             Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            String projectName = (String) body.get("name");
            String projectDescription = (String) body.get("description");
            String startDateStr = (String) body.get("startDate");

            if (projectName == null || projectName.trim().isEmpty() || startDateStr == null) {
                return ResponseEntity.badRequest().build();
            }

            LocalDate startDate = LocalDate.parse(startDateStr);
            Project project = projectTemplateService.createProjectFromTemplate(
                    id, projectName, projectDescription, startDate, user
            );

            return ResponseEntity.ok(Map.of(
                    "id", project.getId(),
                    "name", project.getName(),
                    "message", "Проект успешно создан из шаблона"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> templateToMap(ProjectTemplate template) {
        return Map.of(
                "id", template.getId(),
                "name", template.getName(),
                "description", template.getDescription() != null ? template.getDescription() : "",
                "category", template.getCategory(),
                "taskCount", template.getTasks().size(),
                "createdAt", template.getCreatedAt()
        );
    }
}
