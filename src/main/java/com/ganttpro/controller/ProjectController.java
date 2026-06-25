package com.ganttpro.controller;

import com.ganttpro.dto.ProjectForm;
import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ProjectController(ProjectService projectService, UserRepository userRepository) {
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public String list(Authentication authentication, Model model) {
        return "redirect:/dashboard";
    }

    @GetMapping("/new")
    public String newProject(Model model) {
        model.addAttribute("projectForm", new ProjectForm());
        return "project/form";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = projectService.getProject(id, user);
        if (project.isEmpty()) return "redirect:/dashboard";

        model.addAttribute("projectId", id);
        return "project/view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = projectService.getProject(id, user);
        if (project.isEmpty()) return "redirect:/dashboard";

        if (!projectService.canEdit(project.get(), user)) {
            return "redirect:/dashboard";
        }

        ProjectForm form = new ProjectForm();
        form.setId(project.get().getId());
        form.setName(project.get().getName());
        form.setDescription(project.get().getDescription());
        form.setStartDate(project.get().getStartDate());
        form.setEndDate(project.get().getEndDate());

        model.addAttribute("projectForm", form);
        return "project/form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            projectService.deleteProject(id, user);
        } catch (Exception e) {
            return "redirect:/dashboard";
        }

        return "redirect:/dashboard";
    }
}
