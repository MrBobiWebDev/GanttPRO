package com.ganttpro.service;

import com.ganttpro.dto.CriticalPathResult;
import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.TaskRepository;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CriticalPathServiceTests {

    @Autowired
    private CriticalPathService criticalPathService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskDependencyService taskDependencyService;

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        taskDependencyRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("User", "user@test.com", "password");
        userRepository.save(user);

        LocalDate today = LocalDate.now();
        project = new Project("Test Project", "Description", today, today.plusDays(30), user);
        projectRepository.save(project);
    }

    @Test
    void testCriticalPathWithoutDependencies() {
        LocalDate today = LocalDate.now();
        Task task1 = new Task(project, "Task 1", "Description", today, today.plusDays(5), "User");
        taskRepository.save(task1);

        CriticalPathResult result = criticalPathService.calculateCriticalPath(project);

        assertFalse(result.getHasCycle());
        assertNotNull(result.getTaskIds());
        assertTrue(result.getTotalDurationDays() > 0);
    }

    @Test
    void testCriticalPathWithLinearDependencies() throws Exception {
        LocalDate today = LocalDate.now();
        Task task1 = new Task(project, "Task 1", "Description", today, today.plusDays(4), "User");
        Task task2 = new Task(project, "Task 2", "Description", today.plusDays(5), today.plusDays(9), "User");
        Task task3 = new Task(project, "Task 3", "Description", today.plusDays(10), today.plusDays(14), "User");

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        taskDependencyService.createDependency(project.getId(), task3.getId(), task2.getId());

        CriticalPathResult result = criticalPathService.calculateCriticalPath(project);

        assertFalse(result.getHasCycle());
        assertEquals(15, result.getTotalDurationDays());
    }

    @Test
    void testCyclicDependencyDetection() throws Exception {
        LocalDate today = LocalDate.now();
        Task task1 = new Task(project, "Task 1", "Description", today, today.plusDays(5), "User");
        Task task2 = new Task(project, "Task 2", "Description", today.plusDays(6), today.plusDays(10), "User");
        Task task3 = new Task(project, "Task 3", "Description", today.plusDays(11), today.plusDays(15), "User");

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        taskDependencyService.createDependency(project.getId(), task3.getId(), task2.getId());

        try {
            taskDependencyService.createDependency(project.getId(), task1.getId(), task3.getId());
            fail("Expected exception for cyclic dependency");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("циклическую"));
        }
    }

    @Test
    void testEmptyProject() {
        CriticalPathResult result = criticalPathService.calculateCriticalPath(project);

        assertEquals(0, result.getTaskIds().size());
    }
}
