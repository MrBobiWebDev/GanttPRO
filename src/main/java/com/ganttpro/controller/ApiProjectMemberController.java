package com.ganttpro.controller;

import com.ganttpro.model.Project;
import com.ganttpro.model.ProjectMember;
import com.ganttpro.model.ProjectRole;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectMemberService;
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
@RequestMapping("/api/projects/{projectId}/members")
public class ApiProjectMemberController {
    private final ProjectMemberService projectMemberService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ApiProjectMemberController(ProjectMemberService projectMemberService,
                                    ProjectService projectService,
                                    UserRepository userRepository) {
        this.projectMemberService = projectMemberService;
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
                                                          Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Optional<Project> project = projectService.getProject(projectId, user);
        if (project.isEmpty()) return ResponseEntity.status(404).build();

        try {
            List<Map<String, Object>> members = projectMemberService.getProjectMembers(projectId).stream()
                    .map(this::memberToMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addMember(@PathVariable Long projectId,
                                                         @RequestBody Map<String, String> body,
                                                         Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            String userEmail = body.get("userEmail");
            ProjectRole role = ProjectRole.valueOf(body.getOrDefault("role", "VIEWER"));
            ProjectMember member = projectMemberService.addMember(projectId, userEmail, role, user);
            return ResponseEntity.ok(memberToMap(member));
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable Long projectId,
                                                          @PathVariable Long memberId,
                                                          @RequestBody Map<String, String> body,
                                                          Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            ProjectRole role = ProjectRole.valueOf(body.get("role"));
            ProjectMember member = projectMemberService.updateRole(projectId, memberId, role, user);
            return ResponseEntity.ok(memberToMap(member));
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long projectId,
                                            @PathVariable Long memberId,
                                            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            projectMemberService.removeMember(projectId, memberId, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    private Map<String, Object> memberToMap(ProjectMember member) {
        return Map.of(
                "id", member.getId(),
                "userId", member.getUser().getId(),
                "userName", member.getUser().getName(),
                "userEmail", member.getUser().getEmail(),
                "role", member.getRole().name(),
                "createdAt", member.getCreatedAt()
        );
    }
}
