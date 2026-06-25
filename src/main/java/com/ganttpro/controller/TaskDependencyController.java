package com.ganttpro.controller;

import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskDependency;
import com.ganttpro.model.User;
import com.ganttpro.repository.UserRepository;
import com.ganttpro.service.ProjectMemberService;
import com.ganttpro.service.ProjectService;
import com.ganttpro.service.TaskDependencyService;
import com.ganttpro.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/projects/{projectId}/tasks/{taskId}/dependencies")
public class TaskDependencyController {
    private final TaskDependencyService taskDependencyService;
    private final TaskService taskService;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final UserRepository userRepository;

    public TaskDependencyController(TaskDependencyService taskDependencyService,
                                   TaskService taskService,
                                   ProjectService projectService,
                                   ProjectMemberService projectMemberService,
                                   UserRepository userRepository) {
        this.taskDependencyService = taskDependencyService;
        this.taskService = taskService;
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
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
    public String list(@PathVariable Long projectId, @PathVariable Long taskId,
                      Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Optional<Project> project = getProjectForUser(projectId, authentication);
        if (project.isEmpty()) return "redirect:/dashboard";

        Optional<Task> task = taskService.getTask(taskId, project.get());
        if (task.isEmpty()) return "redirect:/projects/" + projectId;

        if (!projectMemberService.canEdit(project.get(), user)) {
            return "redirect:/projects/" + projectId;
        }

        try {
            List<TaskDependency> dependencies = taskDependencyService.getTaskDependencies(projectId, taskId);
            List<Task> availableTasks = taskService.getProjectTasks(project.get()).stream()
                    .filter(t -> !t.getId().equals(taskId))
                    .toList();

            model.addAttribute("projectId", projectId);
            model.addAttribute("taskId", taskId);
            model.addAttribute("task", task.get());
            model.addAttribute("dependencies", dependencies);
            model.addAttribute("availableTasks", availableTasks);
        } catch (Exception e) {
            return "redirect:/projects/" + projectId;
        }

        return "task/dependencies";
    }

    @PostMapping("/add")
    public String addDependency(@PathVariable Long projectId, @PathVariable Long taskId,
                               @RequestParam Long dependsOnTaskId,
                               Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            taskDependencyService.createDependency(projectId, taskId, dependsOnTaskId);
        } catch (Exception e) {
        }

        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/dependencies";
    }

    @PostMapping("/{dependencyId}/remove")
    public String removeDependency(@PathVariable Long projectId, @PathVariable Long taskId,
                                  @PathVariable Long dependencyId,
                                  Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            taskDependencyService.removeDependency(projectId, dependencyId);
        } catch (Exception e) {
        }

        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/dependencies";
    }
}
