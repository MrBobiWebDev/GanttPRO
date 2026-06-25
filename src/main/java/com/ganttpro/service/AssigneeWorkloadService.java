package com.ganttpro.service;

import com.ganttpro.dto.AssigneeWorkload;
import com.ganttpro.dto.CriticalPathResult;
import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskPriority;
import com.ganttpro.model.TaskStatus;
import com.ganttpro.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssigneeWorkloadService {
    private final TaskRepository taskRepository;
    private final CriticalPathService criticalPathService;

    public AssigneeWorkloadService(TaskRepository taskRepository,
                                  CriticalPathService criticalPathService) {
        this.taskRepository = taskRepository;
        this.criticalPathService = criticalPathService;
    }

    public List<AssigneeWorkload> getProjectWorkload(Project project) {
        List<Task> tasks = taskRepository.findByProject(project);
        CriticalPathResult criticalPath = criticalPathService.calculateCriticalPath(project);
        Set<Long> criticalTaskIds = new HashSet<>(criticalPath.getTaskIds());

        Map<String, AssigneeWorkload> workloadMap = new HashMap<>();

        for (Task task : tasks) {
            String assigneeName = task.getAssigneeName();
            if (assigneeName == null || assigneeName.trim().isEmpty()) {
                assigneeName = "Без исполнителя";
            }

            AssigneeWorkload workload = workloadMap.computeIfAbsent(assigneeName, AssigneeWorkload::new);

            workload.setTotalTasks(workload.getTotalTasks() + 1);

            if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                workload.setInProgressTasks(workload.getInProgressTasks() + 1);
            }

            if (task.getStatus() == TaskStatus.DONE) {
                workload.setDoneTasks(workload.getDoneTasks() + 1);
            }

            if (task.getStatus() != TaskStatus.DONE && task.getEndDate().isBefore(LocalDate.now())) {
                workload.setOverdueTasks(workload.getOverdueTasks() + 1);
            }

            if (task.getPriority() == TaskPriority.CRITICAL) {
                workload.setCriticalPriorityTasks(workload.getCriticalPriorityTasks() + 1);
            }

            if (criticalTaskIds.contains(task.getId())) {
                workload.setCriticalPathTasks(workload.getCriticalPathTasks() + 1);
            }
        }

        // Calculate average progress for each assignee
        for (String assigneeName : workloadMap.keySet()) {
            String finalAssigneeName = assigneeName;
            List<Task> assigneeTasks = tasks.stream()
                    .filter(t -> {
                        String tn = t.getAssigneeName();
                        if (tn == null || tn.trim().isEmpty()) {
                            return finalAssigneeName.equals("Без исполнителя");
                        }
                        return tn.equals(finalAssigneeName);
                    })
                    .collect(Collectors.toList());

            if (!assigneeTasks.isEmpty()) {
                double avgProgress = assigneeTasks.stream()
                        .mapToInt(Task::getProgress)
                        .average()
                        .orElse(0.0);
                workloadMap.get(assigneeName).setAverageProgress(Math.round(avgProgress * 100.0) / 100.0);
            }
        }

        return new ArrayList<>(workloadMap.values());
    }

    public AssigneeWorkload getAssigneeWorkload(Project project, String assigneeName) {
        List<AssigneeWorkload> allWorkload = getProjectWorkload(project);
        String searchName = assigneeName == null || assigneeName.trim().isEmpty() ? "Без исполнителя" : assigneeName;

        return allWorkload.stream()
                .filter(w -> w.getAssigneeName().equals(searchName))
                .findFirst()
                .orElse(new AssigneeWorkload(searchName));
    }
}
