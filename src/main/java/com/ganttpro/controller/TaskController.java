package com.ganttpro.controller;

import com.ganttpro.dto.TaskForm;
import com.ganttpro.model.Task;
import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.TaskService;
import com.ganttpro.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public TaskController(TaskService taskService, ProjectService projectService,
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

    @GetMapping("/{taskId}")
    public String view(@PathVariable Long projectId, @PathVariable Long taskId,
                      Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return "redirect:/dashboard";

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return "redirect:/projects/" + projectId;

        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        return "task/view";
    }

    @GetMapping("/new")
    public String newTask(@PathVariable Long projectId, Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return "redirect:/dashboard";

        if (!projectService.canEdit(project.get(), user)) {
            return "redirect:/projects/" + projectId;
        }

        model.addAttribute("projectId", projectId);
        model.addAttribute("taskForm", new TaskForm());
        return "task/form";
    }

    @GetMapping("/{taskId}/edit")
    public String edit(@PathVariable Long projectId, @PathVariable Long taskId,
                      Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return "redirect:/dashboard";

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return "redirect:/projects/" + projectId;

        if (!projectService.canEdit(project.get(), user)) {
            return "redirect:/projects/" + projectId;
        }

        TaskForm form = new TaskForm();
        form.setId(task.get().getId());
        form.setTitle(task.get().getTitle());
        form.setDescription(task.get().getDescription());
        form.setStartDate(task.get().getStartDate());
        form.setEndDate(task.get().getEndDate());
        form.setStatus(task.get().getStatus());
        form.setProgress(task.get().getProgress());
        form.setAssigneeName(task.get().getAssigneeName());
        form.setPriority(task.get().getPriority());

        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("taskForm", form);
        return "task/form";
    }

    @PostMapping("/{taskId}/delete")
    public String delete(@PathVariable Long projectId, @PathVariable Long taskId,
                        Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isPresent()) {
            try {
                taskService.deleteTask(taskId, user);
            } catch (Exception e) {
                return "redirect:/projects/" + projectId;
            }
        }

        return "redirect:/projects/" + projectId;
    }
}
