package com.ganttpro.service;

import com.ganttpro.dto.PlanningIssue;
import com.ganttpro.model.*;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanningIssueService {
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;

    public PlanningIssueService(TaskRepository taskRepository,
                               TaskDependencyRepository taskDependencyRepository) {
        this.taskRepository = taskRepository;
        this.taskDependencyRepository = taskDependencyRepository;
    }

    public List<PlanningIssue> getProjectIssues(Project project) {
        List<PlanningIssue> issues = new ArrayList<>();
        List<Task> tasks = taskRepository.findByProject(project);

        for (Task task : tasks) {
            issues.addAll(getTaskIssues(task));
        }

        return issues;
    }

    public List<PlanningIssue> getTaskIssues(Task task) {
        List<PlanningIssue> issues = new ArrayList<>();

        // DEPENDENCY_DATE_CONFLICT
        if (hasDependencyDateConflict(task)) {
            issues.add(new PlanningIssue(
                    PlanningIssueType.DEPENDENCY_DATE_CONFLICT,
                    task.getId(),
                    task.getTitle(),
                    PlanningIssueSeverity.CRITICAL,
                    "Задача начинается раньше завершения предшествующей задачи"
            ));
        }

        // TASK_OUTSIDE_PROJECT_DATES
        if (isTaskOutsideProjectDates(task)) {
            issues.add(new PlanningIssue(
                    PlanningIssueType.TASK_OUTSIDE_PROJECT_DATES,
                    task.getId(),
                    task.getTitle(),
                    PlanningIssueSeverity.WARNING,
                    "Задача выходит за границы дат проекта"
            ));
        }

        // OVERDUE_TASK
        if (isTaskOverdue(task)) {
            issues.add(new PlanningIssue(
                    PlanningIssueType.OVERDUE_TASK,
                    task.getId(),
                    task.getTitle(),
                    PlanningIssueSeverity.CRITICAL,
                    "Задача просрочена"
            ));
        }

        // TASK_WITHOUT_ASSIGNEE
        if (task.getAssigneeName() == null || task.getAssigneeName().trim().isEmpty()) {
            issues.add(new PlanningIssue(
                    PlanningIssueType.TASK_WITHOUT_ASSIGNEE,
                    task.getId(),
                    task.getTitle(),
                    PlanningIssueSeverity.INFO,
                    "Задача не назначена на исполнителя"
            ));
        }

        // ZERO_DURATION_TASK
        if (isZeroDurationTask(task)) {
            issues.add(new PlanningIssue(
                    PlanningIssueType.ZERO_DURATION_TASK,
                    task.getId(),
                    task.getTitle(),
                    PlanningIssueSeverity.INFO,
                    "Задача имеет нулевую длительность"
            ));
        }

        // BLOCKED_WITHOUT_REASON
        if (isBlockedWithoutReason(task)) {
            issues.add(new PlanningIssue(
                    PlanningIssueType.BLOCKED_WITHOUT_REASON,
                    task.getId(),
                    task.getTitle(),
                    PlanningIssueSeverity.WARNING,
                    "Заблокированная задача без объяснения причины"
            ));
        }

        return issues;
    }

    private boolean hasDependencyDateConflict(Task task) {
        List<TaskDependency> dependencies = taskDependencyRepository.findByTaskAndProject(task, task.getProject());
        for (TaskDependency dep : dependencies) {
            Task predecessor = dep.getDependsOnTask();
            if (predecessor.getEndDate().isAfter(task.getStartDate()) ||
                predecessor.getEndDate().equals(task.getStartDate())) {
                return true;
            }
        }
        return false;
    }

    private boolean isTaskOutsideProjectDates(Task task) {
        Project project = task.getProject();
        if (project.getStartDate() != null && task.getStartDate().isBefore(project.getStartDate())) {
            return true;
        }
        if (project.getEndDate() != null && task.getEndDate().isAfter(project.getEndDate())) {
            return true;
        }
        return false;
    }

    private boolean isTaskOverdue(Task task) {
        if (task.getStatus() == TaskStatus.DONE) {
            return false;
        }
        return task.getEndDate().isBefore(LocalDate.now());
    }

    private boolean isZeroDurationTask(Task task) {
        return task.getStartDate().equals(task.getEndDate());
    }

    private boolean isBlockedWithoutReason(Task task) {
        if (task.getStatus() != TaskStatus.BLOCKED) {
            return false;
        }
        String description = task.getDescription();
        return description == null || description.trim().isEmpty();
    }

    public long getCriticalIssueCount(Project project) {
        return getProjectIssues(project).stream()
                .filter(issue -> issue.getSeverity() == PlanningIssueSeverity.CRITICAL)
                .count();
    }

    public long getWarningIssueCount(Project project) {
        return getProjectIssues(project).stream()
                .filter(issue -> issue.getSeverity() == PlanningIssueSeverity.WARNING)
                .count();
    }
}
