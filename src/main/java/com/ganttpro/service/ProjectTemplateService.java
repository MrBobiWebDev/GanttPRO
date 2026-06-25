package com.ganttpro.service;

import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.ProjectTemplateRepository;
import com.ganttpro.repository.ProjectTemplateTaskRepository;
import com.ganttpro.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectTemplateService {
    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateTaskRepository templateTaskRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectTemplateService(ProjectTemplateRepository templateRepository,
                                 ProjectTemplateTaskRepository templateTaskRepository,
                                 ProjectRepository projectRepository,
                                 TaskRepository taskRepository) {
        this.templateRepository = templateRepository;
        this.templateTaskRepository = templateTaskRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public List<ProjectTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    public List<ProjectTemplate> getTemplatesByCategory(String category) {
        return templateRepository.findByCategory(category);
    }

    public Optional<ProjectTemplate> getTemplate(Long templateId) {
        return templateRepository.findById(templateId);
    }

    public Project createProjectFromTemplate(Long templateId, String projectName, String projectDescription,
                                           LocalDate startDate, User owner) throws Exception {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new Exception("Шаблон проекта не найден"));

        List<ProjectTemplateTask> templateTasks = templateTaskRepository
                .findByTemplateOrderByOrderIndexAsc(template);

        if (templateTasks.isEmpty()) {
            throw new Exception("Шаблон проекта не содержит задач");
        }

        LocalDate projectEndDate = calculateProjectEndDate(startDate, templateTasks);

        Project project = new Project(projectName, projectDescription, startDate, projectEndDate, owner);
        project = projectRepository.save(project);

        for (ProjectTemplateTask templateTask : templateTasks) {
            LocalDate taskStartDate = startDate.plusDays(templateTask.getOffsetStartDays());
            LocalDate taskEndDate = taskStartDate.plusDays(templateTask.getDurationDays() - 1);

            Task task = new Task(
                    project,
                    templateTask.getTitle(),
                    templateTask.getDescription(),
                    taskStartDate,
                    taskEndDate,
                    templateTask.getAssigneeName()
            );

            task.setStatus(templateTask.getStatus());
            task.setPriority(templateTask.getPriority());
            taskRepository.save(task);
        }

        return project;
    }

    private LocalDate calculateProjectEndDate(LocalDate startDate, List<ProjectTemplateTask> templateTasks) {
        long maxOffset = templateTasks.stream()
                .mapToLong(t -> (long) t.getOffsetStartDays() + t.getDurationDays() - 1)
                .max()
                .orElse(0);

        return startDate.plusDays(maxOffset);
    }

    public ProjectTemplate createTemplate(String name, String description, String category) {
        ProjectTemplate template = new ProjectTemplate(name, description, category);
        return templateRepository.save(template);
    }

    public ProjectTemplateTask addTaskToTemplate(ProjectTemplate template, String title,
                                                  String description, Integer offsetStartDays,
                                                  Integer durationDays, TaskStatus status,
                                                  TaskPriority priority, String assigneeName,
                                                  Integer orderIndex) {
        ProjectTemplateTask task = new ProjectTemplateTask(
                template, title, description, offsetStartDays, durationDays,
                status, priority, assigneeName, orderIndex
        );
        return templateTaskRepository.save(task);
    }
}
