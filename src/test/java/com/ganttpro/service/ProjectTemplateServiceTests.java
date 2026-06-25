package com.ganttpro.service;

import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.ProjectTemplateRepository;
import com.ganttpro.repository.ProjectTemplateTaskRepository;
import com.ganttpro.repository.TaskRepository;
import com.ganttpro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectTemplateServiceTests {

    @Autowired
    private ProjectTemplateService projectTemplateService;

    @Autowired
    private ProjectTemplateRepository templateRepository;

    @Autowired
    private ProjectTemplateTaskRepository templateTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        templateTaskRepository.deleteAll();
        templateRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("User", "user@test.com", "password");
        userRepository.save(user);
    }

    @Test
    void testCreateProjectFromTemplate() throws Exception {
        ProjectTemplate template = projectTemplateService.createTemplate(
            "Web Project", "Standard web project template", "Development"
        );

        projectTemplateService.addTaskToTemplate(template, "Design",
            "Design the application", 0, 5, TaskStatus.TODO, TaskPriority.MEDIUM, null, 0);
        projectTemplateService.addTaskToTemplate(template, "Development",
            "Develop the application", 5, 10, TaskStatus.TODO, TaskPriority.MEDIUM, null, 1);
        projectTemplateService.addTaskToTemplate(template, "Testing",
            "Test the application", 15, 5, TaskStatus.TODO, TaskPriority.MEDIUM, null, 2);

        LocalDate startDate = LocalDate.now();
        Project project = projectTemplateService.createProjectFromTemplate(
            template.getId(), "My Web Project", "My new web project", startDate, user
        );

        assertNotNull(project);
        assertEquals("My Web Project", project.getName());
        assertEquals(3, project.getTasks().size());
        assertEquals(startDate, project.getStartDate());
    }

    @Test
    void testProjectEndDateCalculation() throws Exception {
        ProjectTemplate template = projectTemplateService.createTemplate(
            "Test", "Test template", "Test"
        );

        projectTemplateService.addTaskToTemplate(template, "Task 1", null, 0, 5, TaskStatus.TODO, TaskPriority.MEDIUM, null, 0);
        projectTemplateService.addTaskToTemplate(template, "Task 2", null, 5, 10, TaskStatus.TODO, TaskPriority.MEDIUM, null, 1);
        projectTemplateService.addTaskToTemplate(template, "Task 3", null, 12, 8, TaskStatus.TODO, TaskPriority.MEDIUM, null, 2);

        LocalDate startDate = LocalDate.now();
        Project project = projectTemplateService.createProjectFromTemplate(
            template.getId(), "Test Project", "Description", startDate, user
        );

        LocalDate expectedEndDate = startDate.plusDays(19);
        assertEquals(expectedEndDate, project.getEndDate());
    }

    @Test
    void testTemplateTasksPreservePriority() throws Exception {
        ProjectTemplate template = projectTemplateService.createTemplate(
            "Priority Test", "Test priority preservation", "Test"
        );

        projectTemplateService.addTaskToTemplate(template, "Critical Task", null,
            0, 5, TaskStatus.TODO, TaskPriority.CRITICAL, null, 0);

        LocalDate startDate = LocalDate.now();
        Project project = projectTemplateService.createProjectFromTemplate(
            template.getId(), "Test", "Description", startDate, user
        );

        Task task = project.getTasks().get(0);
        assertEquals(TaskPriority.CRITICAL, task.getPriority());
    }

    @Test
    void testGetAllTemplates() {
        projectTemplateService.createTemplate("Template 1", "Desc 1", "Category 1");
        projectTemplateService.createTemplate("Template 2", "Desc 2", "Category 2");

        List<ProjectTemplate> templates = projectTemplateService.getAllTemplates();
        assertTrue(templates.size() >= 2);
    }
}
