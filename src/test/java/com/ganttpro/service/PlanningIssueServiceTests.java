package com.ganttpro.service;

import com.ganttpro.dto.PlanningIssue;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlanningIssueServiceTests {

    @Autowired
    private PlanningIssueService planningIssueService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private UserRepository userRepository;

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
        project = new Project("Test Project", "Description", today.minusDays(5), today.plusDays(30), user);
        projectRepository.save(project);
    }

    @Test
    void testDetectOverdueTask() {
        LocalDate today = LocalDate.now();
        Task overdueTask = new Task(project, "Overdue Task", "Description",
            today.minusDays(5), today.minusDays(2), "User");
        overdueTask.setStatus(TaskStatus.TODO);
        taskRepository.save(overdueTask);

        List<PlanningIssue> issues = planningIssueService.getTaskIssues(overdueTask);
        assertTrue(issues.stream().anyMatch(i -> i.getType() == PlanningIssueType.OVERDUE_TASK));
    }

    @Test
    void testDetectTaskOutsideProjectDates() {
        LocalDate today = LocalDate.now();
        Task outsideTask = new Task(project, "Outside Task", "Description",
            today.minusDays(10), today.minusDays(8), "User");
        taskRepository.save(outsideTask);

        List<PlanningIssue> issues = planningIssueService.getTaskIssues(outsideTask);
        assertTrue(issues.stream().anyMatch(i -> i.getType() == PlanningIssueType.TASK_OUTSIDE_PROJECT_DATES));
    }

    @Test
    void testDetectTaskWithoutAssignee() {
        LocalDate today = LocalDate.now();
        Task unassignedTask = new Task(project, "Unassigned Task", "Description",
            today, today.plusDays(5), null);
        taskRepository.save(unassignedTask);

        List<PlanningIssue> issues = planningIssueService.getTaskIssues(unassignedTask);
        assertTrue(issues.stream().anyMatch(i -> i.getType() == PlanningIssueType.TASK_WITHOUT_ASSIGNEE));
    }

    @Test
    void testDetectZeroDurationTask() {
        LocalDate today = LocalDate.now();
        Task zeroTask = new Task(project, "Zero Duration Task", "Description",
            today, today, "User");
        taskRepository.save(zeroTask);

        List<PlanningIssue> issues = planningIssueService.getTaskIssues(zeroTask);
        assertTrue(issues.stream().anyMatch(i -> i.getType() == PlanningIssueType.ZERO_DURATION_TASK));
    }

    @Test
    void testDetectBlockedTaskWithoutReason() {
        LocalDate today = LocalDate.now();
        Task blockedTask = new Task(project, "Blocked Task", "",
            today, today.plusDays(5), "User");
        blockedTask.setStatus(TaskStatus.BLOCKED);
        taskRepository.save(blockedTask);

        List<PlanningIssue> issues = planningIssueService.getTaskIssues(blockedTask);
        assertTrue(issues.stream().anyMatch(i -> i.getType() == PlanningIssueType.BLOCKED_WITHOUT_REASON));
    }

    @Test
    void testGetCriticalIssueCount() {
        LocalDate today = LocalDate.now();
        Task overdueTask = new Task(project, "Overdue Task", "Description",
            today.minusDays(5), today.minusDays(2), "User");
        overdueTask.setStatus(TaskStatus.TODO);
        taskRepository.save(overdueTask);

        long count = planningIssueService.getCriticalIssueCount(project);
        assertTrue(count > 0);
    }
}
