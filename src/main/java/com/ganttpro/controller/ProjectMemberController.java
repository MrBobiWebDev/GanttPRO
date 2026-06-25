package com.ganttpro.controller;

import com.ganttpro.model.Project;
import com.ganttpro.model.ProjectMember;
import com.ganttpro.model.ProjectRole;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectMemberService;
import com.ganttpro.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ProjectMemberController(ProjectMemberService projectMemberService,
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

    private Optional<Project> getProjectForUser(@PathVariable Long projectId,
                                                Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return Optional.empty();
        return projectService.getProject(projectId, user);
    }

    @GetMapping
    public String list(@PathVariable Long projectId, Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return "redirect:/dashboard";

        if (!projectMemberService.isOwner(project.get(), user)) {
            return "redirect:/projects/" + projectId;
        }

        try {
            List<ProjectMember> members = projectMemberService.getProjectMembers(projectId);
            model.addAttribute("projectId", projectId);
            model.addAttribute("project", project.get());
            model.addAttribute("members", members);
            model.addAttribute("roles", ProjectRole.values());
        } catch (Exception e) {
            return "redirect:/projects/" + projectId;
        }

        return "project/members";
    }

    @PostMapping("/add")
    public String addMember(@PathVariable Long projectId,
                           @RequestParam String userEmail,
                           @RequestParam ProjectRole role,
                           Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            projectMemberService.addMember(projectId, userEmail, role, user);
        } catch (Exception e) {
        }

        return "redirect:/projects/" + projectId + "/members";
    }

    @PostMapping("/{memberId}/update-role")
    public String updateRole(@PathVariable Long projectId,
                            @PathVariable Long memberId,
                            @RequestParam ProjectRole role,
                            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            projectMemberService.updateRole(projectId, memberId, role, user);
        } catch (Exception e) {
        }

        return "redirect:/projects/" + projectId + "/members";
    }

    @PostMapping("/{memberId}/remove")
    public String removeMember(@PathVariable Long projectId,
                              @PathVariable Long memberId,
                              Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            projectMemberService.removeMember(projectId, memberId, user);
        } catch (Exception e) {
        }

        return "redirect:/projects/" + projectId + "/members";
    }
}
