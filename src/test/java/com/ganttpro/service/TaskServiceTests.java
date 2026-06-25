package com.ganttpro.service;

import com.ganttpro.dto.ProjectForm;
import com.ganttpro.dto.TaskForm;
import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskStatus;
import com.ganttpro.model.User;
import com.ganttpro.model.UserRole;
import com.ganttpro.repository.ProjectMemberRepository;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.TaskRepository;
import com.ganttpro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceTests {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        taskDependencyRepository.deleteAll();
        projectMemberRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("Test User", "test@example.com",
                passwordEncoder.encode("password123"));
        testUser.setRole(UserRole.USER);
        testUser = userRepository.save(testUser);

        ProjectForm projectForm = new ProjectForm("Test Project", "Description",
                LocalDate.now(), LocalDate.now().plusDays(30));
        testProject = projectService.createProject(projectForm, testUser);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskForm form = new TaskForm(
                "Test Task",
                "Task Description",
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "John Doe"
        );

        Task task = taskService.createTask(testProject, form, testUser);

        assertNotNull(task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(0, task.getProgress());
    }

    @Test
    void testGetProjectTasks() throws Exception {
        TaskForm form1 = new TaskForm("Task 1", "Desc 1",
                LocalDate.now(), LocalDate.now().plusDays(5), "User 1");
        TaskForm form2 = new TaskForm("Task 2", "Desc 2",
                LocalDate.now(), LocalDate.now().plusDays(10), "User 2");

        taskService.createTask(testProject, form1, testUser);
        taskService.createTask(testProject, form2, testUser);

        List<Task> tasks = taskService.getProjectTasks(testProject);

        assertEquals(2, tasks.size());
    }

    @Test
    void testGetTask() throws Exception {
        TaskForm form = new TaskForm("Test Task", "Description",
                LocalDate.now(), LocalDate.now().plusDays(5), "John");
        Task created = taskService.createTask(testProject, form, testUser);

        Optional<Task> found = taskService.getTask(created.getId(), testProject);

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskForm form = new TaskForm("Original Task", "Original Desc",
                LocalDate.now(), LocalDate.now().plusDays(5), "Original");
        Task task = taskService.createTask(testProject, form, testUser);

        TaskForm updatedForm = new TaskForm("Updated Task", "Updated Desc",
                LocalDate.now(), LocalDate.now().plusDays(10), "Updated");
        updatedForm.setStatus(TaskStatus.IN_PROGRESS);
        updatedForm.setProgress(50);

        Task updated = taskService.updateTask(task, updatedForm, testUser);

        assertEquals("Updated Task", updated.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(50, updated.getProgress());
    }

    @Test
    void testUpdateTaskStatusDone() throws Exception {
        TaskForm form = new TaskForm("Test Task", "Description",
                LocalDate.now(), LocalDate.now().plusDays(5), "John");
        Task task = taskService.createTask(testProject, form, testUser);

        TaskForm updatedForm = new TaskForm("Test Task", "Description",
                LocalDate.now(), LocalDate.now().plusDays(5), "John");
        updatedForm.setStatus(TaskStatus.DONE);
        updatedForm.setProgress(50); // Should be set to 100

        Task updated = taskService.updateTask(task, updatedForm, testUser);

        assertEquals(TaskStatus.DONE, updated.getStatus());
        assertEquals(100, updated.getProgress());
    }

    @Test
    void testDeleteTask() throws Exception {
        TaskForm form = new TaskForm("Test Task", "Description",
                LocalDate.now(), LocalDate.now().plusDays(5), "John");
        Task task = taskService.createTask(testProject, form, testUser);
        Long taskId = task.getId();
        Long projectId = testProject.getId();

        taskService.deleteTask(taskId, testUser);

        Optional<Project> projectOpt = projectRepository.findById(projectId);
        assertTrue(projectOpt.isPresent());
        Optional<Task> found = taskService.getTask(taskId, projectOpt.get());
        assertTrue(found.isEmpty());
    }

    @Test
    void testValidateDates() {
        TaskForm validForm = new TaskForm("Task", "Desc",
                LocalDate.now(), LocalDate.now().plusDays(5), "User");

        assertTrue(taskService.validateDates(validForm));
    }

    @Test
    void testValidateDatesWithInvalidDates() {
        TaskForm invalidForm = new TaskForm("Task", "Desc",
                LocalDate.now().plusDays(5), LocalDate.now(), "User");

        assertFalse(taskService.validateDates(invalidForm));
    }

    @Test
    void testValidateProgress() {
        assertTrue(taskService.validateProgress(0));
        assertTrue(taskService.validateProgress(50));
        assertTrue(taskService.validateProgress(100));
        assertFalse(taskService.validateProgress(-1));
        assertFalse(taskService.validateProgress(101));
    }
}
