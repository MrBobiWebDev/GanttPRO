package com.ganttpro.service;

import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectMemberRepository;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.TaskRepository;
import com.ganttpro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskDependencyServiceTests {

    @Autowired
    private TaskDependencyService taskDependencyService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private User user;
    private Project project;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        taskDependencyRepository.deleteAll();
        projectMemberRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("User", "user@test.com", "password");
        userRepository.save(user);

        LocalDate today = LocalDate.now();
        project = new Project("Test Project", "Description", today, today.plusDays(30), user);
        projectRepository.save(project);

        task1 = new Task(project, "Task 1", "Description", today, today.plusDays(5), "User1");
        task1.setStatus(TaskStatus.DONE);
        taskRepository.save(task1);

        task2 = new Task(project, "Task 2", "Description", today.plusDays(5), today.plusDays(10), "User2");
        task2.setStatus(TaskStatus.TODO);
        taskRepository.save(task2);

        task3 = new Task(project, "Task 3", "Description", today.plusDays(10), today.plusDays(15), "User3");
        task3.setStatus(TaskStatus.TODO);
        taskRepository.save(task3);
    }

    @Test
    void testCreateDependency() throws Exception {
        TaskDependency dependency = taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        assertNotNull(dependency);
        assertEquals(task2.getId(), dependency.getTask().getId());
        assertEquals(task1.getId(), dependency.getDependsOnTask().getId());
    }

    @Test
    void testCreateSelfDependencyFails() throws Exception {
        assertThrows(Exception.class, () ->
                taskDependencyService.createDependency(project.getId(), task1.getId(), task1.getId())
        );
    }

    @Test
    void testCreateDuplicateDependencyFails() throws Exception {
        taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        assertThrows(Exception.class, () ->
                taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId())
        );
    }

    @Test
    void testCreateCyclicDependencyFails() throws Exception {
        taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        taskDependencyService.createDependency(project.getId(), task3.getId(), task2.getId());
        assertThrows(Exception.class, () ->
                taskDependencyService.createDependency(project.getId(), task1.getId(), task3.getId())
        );
    }

    @Test
    void testRemoveDependency() throws Exception {
        TaskDependency dependency = taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        assertNotNull(dependency.getId());

        taskDependencyService.removeDependency(project.getId(), dependency.getId());
        Optional<TaskDependency> removed = taskDependencyRepository.findById(dependency.getId());
        assertTrue(removed.isEmpty());
    }

    @Test
    void testGetTaskDependencies() throws Exception {
        taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        taskDependencyService.createDependency(project.getId(), task3.getId(), task1.getId());

        var dependencies = taskDependencyService.getTaskDependencies(project.getId(), task2.getId());
        assertEquals(1, dependencies.size());
        assertEquals(task1.getId(), dependencies.get(0).getDependsOnTask().getId());
    }

    @Test
    void testHasSchedulingConflict() throws Exception {
        taskDependencyService.createDependency(project.getId(), task2.getId(), task1.getId());
        assertFalse(taskDependencyService.hasSchedulingConflict(task2));

        LocalDate today = LocalDate.now();
        Task conflictTask = new Task(project, "Conflict Task", "Description", today.plusDays(3), today.plusDays(8), "User");
        taskRepository.save(conflictTask);

        taskDependencyService.createDependency(project.getId(), conflictTask.getId(), task1.getId());
        assertTrue(taskDependencyService.hasSchedulingConflict(conflictTask));
    }

    @Test
    void testGetSchedulingWarning() throws Exception {
        LocalDate today = LocalDate.now();
        Task conflictTask = new Task(project, "Conflict Task", "Description", today.plusDays(3), today.plusDays(8), "User");
        taskRepository.save(conflictTask);

        taskDependencyService.createDependency(project.getId(), conflictTask.getId(), task1.getId());
        String warning = taskDependencyService.getSchedulingWarning(conflictTask);
        assertNotNull(warning);
        assertTrue(warning.contains("раньше завершения"));
    }
}
