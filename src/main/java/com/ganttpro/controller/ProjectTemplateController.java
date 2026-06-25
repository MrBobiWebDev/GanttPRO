package com.ganttpro.controller;

import com.ganttpro.dto.ProjectForm;
import com.ganttpro.model.Project;
import com.ganttpro.model.ProjectTemplate;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectTemplateService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/templates")
public class ProjectTemplateController {
    private final ProjectTemplateService projectTemplateService;
    private final UserRepository userRepository;

    public ProjectTemplateController(ProjectTemplateService projectTemplateService,
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
    public String list(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        List<ProjectTemplate> templates = projectTemplateService.getAllTemplates();
        model.addAttribute("templates", templates);
        return "template/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<ProjectTemplate> template = projectTemplateService.getTemplate(id);
        if (template.isEmpty()) return "redirect:/templates";

        model.addAttribute("template", template.get());
        return "template/view";
    }

    @GetMapping("/{id}/create-project")
    public String createProjectForm(@PathVariable Long id, Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<ProjectTemplate> template = projectTemplateService.getTemplate(id);
        if (template.isEmpty()) return "redirect:/templates";

        model.addAttribute("template", template.get());
        model.addAttribute("projectForm", new ProjectForm());
        return "template/create-project";
    }

    @PostMapping("/{id}/create-project")
    public String createProject(@PathVariable Long id,
                              @ModelAttribute ProjectForm form,
                              Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            Project project = projectTemplateService.createProjectFromTemplate(
                    id, form.getName(), form.getDescription(), form.getStartDate(), user
            );
            return "redirect:/projects/" + project.getId();
        } catch (Exception e) {
            return "redirect:/templates/" + id;
        }
    }
}
