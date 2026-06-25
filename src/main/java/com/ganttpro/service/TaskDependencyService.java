package com.ganttpro.service;

import com.ganttpro.model.*;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.TaskRepository;
import com.ganttpro.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TaskDependencyService {
    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskDependencyService(TaskDependencyRepository taskDependencyRepository,
                               TaskRepository taskRepository,
                               ProjectRepository projectRepository) {
        this.taskDependencyRepository = taskDependencyRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public TaskDependency createDependency(Long projectId, Long taskId, Long dependsOnTaskId) throws Exception {
        if (taskId.equals(dependsOnTaskId)) {
            throw new Exception("Задача не может зависеть от самой себя");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new Exception("Задача не найдена"));

        Task dependsOnTask = taskRepository.findById(dependsOnTaskId)
                .orElseThrow(() -> new Exception("Задача-предшественник не найдена"));

        if (!task.getProject().getId().equals(projectId)) {
            throw new Exception("Задача не входит в этот проект");
        }

        if (!dependsOnTask.getProject().getId().equals(projectId)) {
            throw new Exception("Задача-предшественник не входит в этот проект");
        }

        Optional<TaskDependency> existing = taskDependencyRepository.findByTaskAndDependsOnTaskAndProject(task, dependsOnTask, project);
        if (existing.isPresent()) {
            throw new Exception("Такая зависимость уже существует");
        }

        if (wouldCreateCycle(task, dependsOnTask)) {
            throw new Exception("Это создаст циклическую зависимость");
        }

        TaskDependency dependency = new TaskDependency(project, task, dependsOnTask, DependencyType.FINISH_TO_START);
        return taskDependencyRepository.save(dependency);
    }

    public void removeDependency(Long projectId, Long dependencyId) throws Exception {
        TaskDependency dependency = taskDependencyRepository.findById(dependencyId)
                .orElseThrow(() -> new Exception("Зависимость не найдена"));

        if (!dependency.getProject().getId().equals(projectId)) {
            throw new Exception("Зависимость не входит в этот проект");
        }

        taskDependencyRepository.delete(dependency);
    }

    public List<TaskDependency> getTaskDependencies(Long projectId, Long taskId) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new Exception("Задача не найдена"));

        return taskDependencyRepository.findByTaskAndProject(task, project);
    }

    public List<TaskDependency> getTaskPredecessors(Long projectId, Long taskId) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new Exception("Задача не найдена"));

        return taskDependencyRepository.findByTaskAndProject(task, project);
    }

    public List<TaskDependency> getTaskSuccessors(Long projectId, Long taskId) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new Exception("Задача не найдена"));

        return taskDependencyRepository.findByDependsOnTaskAndProject(task, project);
    }

    public List<TaskDependency> getProjectDependencies(Long projectId) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        return taskDependencyRepository.findByProject(project);
    }

    private boolean wouldCreateCycle(Task task, Task dependsOnTask) {
        Set<Long> visited = new HashSet<>();
        return hasCycleFromTask(dependsOnTask, task.getId(), visited);
    }

    private boolean hasCycleFromTask(Task currentTask, Long targetTaskId, Set<Long> visited) {
        if (currentTask.getId().equals(targetTaskId)) {
            return true;
        }

        if (visited.contains(currentTask.getId())) {
            return false;
        }

        visited.add(currentTask.getId());

        List<TaskDependency> predecessors = taskDependencyRepository.findByTaskAndProject(currentTask, currentTask.getProject());
        for (TaskDependency dep : predecessors) {
            if (hasCycleFromTask(dep.getDependsOnTask(), targetTaskId, visited)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSchedulingConflict(Task task) {
        List<TaskDependency> dependencies = taskDependencyRepository.findByTaskAndProject(task, task.getProject());

        for (TaskDependency dep : dependencies) {
            Task predecessor = dep.getDependsOnTask();
            if (predecessor.getEndDate().isAfter(task.getStartDate())) {
                return true;
            }
        }

        return false;
    }

    public String getSchedulingWarning(Task task) {
        if (hasSchedulingConflict(task)) {
            return "Задача начинается раньше завершения предшествующей задачи";
        }
        return null;
    }
}
