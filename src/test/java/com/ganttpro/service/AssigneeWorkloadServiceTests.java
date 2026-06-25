package com.ganttpro.service;

import com.ganttpro.dto.AssigneeWorkload;
import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectRepository;
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
public class AssigneeWorkloadServiceTests {

    @Autowired
    private AssigneeWorkloadService assigneeWorkloadService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
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
    void testCalculateWorkloadForMultipleAssignees() {
        LocalDate today = LocalDate.now();

        Task task1 = new Task(project, "Task 1", "Description", today, today.plusDays(5), "John");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setProgress(50);
        taskRepository.save(task1);

        Task task2 = new Task(project, "Task 2", "Description", today, today.plusDays(5), "John");
        task2.setStatus(TaskStatus.DONE);
        task2.setProgress(100);
        taskRepository.save(task2);

        Task task3 = new Task(project, "Task 3", "Description", today, today.plusDays(5), "Jane");
        task3.setStatus(TaskStatus.TODO);
        task3.setProgress(0);
        taskRepository.save(task3);

        List<AssigneeWorkload> workload = assigneeWorkloadService.getProjectWorkload(project);

        assertEquals(2, workload.size());
        AssigneeWorkload john = workload.stream()
            .filter(w -> "John".equals(w.getAssigneeName()))
            .findFirst()
            .orElse(null);
        assertNotNull(john);
        assertEquals(2, john.getTotalTasks());
        assertEquals(1, john.getDoneTasks());
        assertEquals(1, john.getInProgressTasks());
    }

    @Test
    void testUnassignedTasksGrouping() {
        LocalDate today = LocalDate.now();

        Task task1 = new Task(project, "Task 1", "Description", today, today.plusDays(5), null);
        task1.setStatus(TaskStatus.TODO);
        taskRepository.save(task1);

        Task task2 = new Task(project, "Task 2", "Description", today, today.plusDays(5), "");
        task2.setStatus(TaskStatus.TODO);
        taskRepository.save(task2);

        List<AssigneeWorkload> workload = assigneeWorkloadService.getProjectWorkload(project);

        AssigneeWorkload unassigned = workload.stream()
            .filter(w -> "Без исполнителя".equals(w.getAssigneeName()))
            .findFirst()
            .orElse(null);
        assertNotNull(unassigned);
        assertEquals(2, unassigned.getTotalTasks());
    }

    @Test
    void testOverdueTasksDetection() {
        LocalDate today = LocalDate.now();

        Task overdueTask = new Task(project, "Overdue Task", "Description",
            today.minusDays(5), today.minusDays(2), "John");
        overdueTask.setStatus(TaskStatus.TODO);
        taskRepository.save(overdueTask);

        List<AssigneeWorkload> workload = assigneeWorkloadService.getProjectWorkload(project);

        AssigneeWorkload john = workload.stream()
            .filter(w -> "John".equals(w.getAssigneeName()))
            .findFirst()
            .orElse(null);
        assertNotNull(john);
        assertEquals(1, john.getOverdueTasks());
    }

    @Test
    void testCriticalPriorityTasksCounting() {
        LocalDate today = LocalDate.now();

        Task criticalTask = new Task(project, "Critical Task", "Description",
            today, today.plusDays(5), "John");
        criticalTask.setPriority(TaskPriority.CRITICAL);
        taskRepository.save(criticalTask);

        List<AssigneeWorkload> workload = assigneeWorkloadService.getProjectWorkload(project);

        AssigneeWorkload john = workload.stream()
            .filter(w -> "John".equals(w.getAssigneeName()))
            .findFirst()
            .orElse(null);
        assertNotNull(john);
        assertEquals(1, john.getCriticalPriorityTasks());
    }
}
