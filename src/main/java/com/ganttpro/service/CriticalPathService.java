package com.ganttpro.service;

import com.ganttpro.dto.CriticalPathResult;
import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskDependency;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CriticalPathService {
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;

    public CriticalPathService(TaskRepository taskRepository,
                              TaskDependencyRepository taskDependencyRepository) {
        this.taskRepository = taskRepository;
        this.taskDependencyRepository = taskDependencyRepository;
    }

    public CriticalPathResult calculateCriticalPath(Project project) {
        List<Task> tasks = taskRepository.findByProject(project);
        List<TaskDependency> dependencies = taskDependencyRepository.findByProject(project);

        if (tasks.isEmpty()) {
            return new CriticalPathResult(new ArrayList<>(), 0L, false, "Проект не содержит задач");
        }

        if (dependencies.isEmpty()) {
            return calculateCriticalPathWithoutDependencies(tasks);
        }

        // Check for cycles
        if (hasCycle(tasks, dependencies)) {
            return new CriticalPathResult(new ArrayList<>(), 0L, true, "Обнаружена циклическая зависимость");
        }

        // Calculate critical path using topological sort and dynamic programming
        Map<Long, Long> taskDurations = calculateTaskDurations(tasks);
        Map<Long, Long> earliestFinish = new HashMap<>();
        Map<Long, List<Long>> paths = new HashMap<>();
        Map<Long, Set<Long>> predecessors = buildPredecessorMap(tasks, dependencies);
        Map<Long, Set<Long>> successors = buildSuccessorMap(tasks, dependencies);

        // Find all tasks with no predecessors (entry tasks)
        Set<Long> entryTasks = tasks.stream()
                .filter(t -> predecessors.getOrDefault(t.getId(), new HashSet<>()).isEmpty())
                .map(Task::getId)
                .collect(Collectors.toSet());

        // Forward pass: calculate earliest finish times
        Queue<Long> queue = new LinkedList<>(entryTasks);
        Set<Long> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            Long taskId = queue.poll();
            if (visited.contains(taskId)) continue;
            visited.add(taskId);

            long duration = taskDurations.get(taskId);
            long earliestStart = 0;

            Set<Long> preds = predecessors.get(taskId);
            if (preds != null && !preds.isEmpty()) {
                earliestStart = preds.stream()
                        .mapToLong(earliestFinish::get)
                        .max()
                        .orElse(0);
            }

            long finish = earliestStart + duration;
            earliestFinish.put(taskId, finish);

            Set<Long> succs = successors.get(taskId);
            if (succs != null) {
                queue.addAll(succs);
            }
        }

        // Find project duration (maximum finish time)
        long projectDuration = earliestFinish.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        // Backward pass: find tasks on critical path
        Set<Long> criticalTasks = new HashSet<>();
        Map<Long, Long> latestFinish = new HashMap<>();

        Set<Long> exitTasks = tasks.stream()
                .filter(t -> successors.getOrDefault(t.getId(), new HashSet<>()).isEmpty())
                .map(Task::getId)
                .collect(Collectors.toSet());

        for (Long taskId : exitTasks) {
            latestFinish.put(taskId, earliestFinish.get(taskId));
            criticalTasks.add(taskId);
        }

        Queue<Long> backwardQueue = new LinkedList<>(exitTasks);
        Set<Long> backwardVisited = new HashSet<>();

        while (!backwardQueue.isEmpty()) {
            Long taskId = backwardQueue.poll();
            if (backwardVisited.contains(taskId)) continue;
            backwardVisited.add(taskId);

            long lf = latestFinish.get(taskId);
            long duration = taskDurations.get(taskId);
            long latestStart = lf - duration;

            Set<Long> preds = predecessors.get(taskId);
            if (preds != null) {
                for (Long predId : preds) {
                    long predEF = earliestFinish.get(predId);
                    if (!latestFinish.containsKey(predId)) {
                        latestFinish.put(predId, latestStart);
                    }
                    long predLF = Math.min(latestFinish.get(predId), latestStart);
                    latestFinish.put(predId, predLF);

                    long predDuration = taskDurations.get(predId);
                    long predLatestStart = predLF - predDuration;
                    long predEarliestStart = predEF - predDuration;

                    if (predLatestStart == predEarliestStart) {
                        criticalTasks.add(predId);
                    }

                    backwardQueue.add(predId);
                }
            }
        }

        List<Long> criticalPath = criticalTasks.stream().sorted().collect(Collectors.toList());

        return new CriticalPathResult(
                criticalPath,
                projectDuration,
                false,
                "Критический путь найден"
        );
    }

    private CriticalPathResult calculateCriticalPathWithoutDependencies(List<Task> tasks) {
        long maxDuration = tasks.stream()
                .mapToLong(t -> java.time.temporal.ChronoUnit.DAYS.between(t.getStartDate(), t.getEndDate()) + 1)
                .max()
                .orElse(1);

        return new CriticalPathResult(
                tasks.stream().map(Task::getId).collect(Collectors.toList()),
                maxDuration,
                false,
                "Критический путь (нет зависимостей)"
        );
    }

    private Map<Long, Long> calculateTaskDurations(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.toMap(
                        Task::getId,
                        t -> java.time.temporal.ChronoUnit.DAYS.between(t.getStartDate(), t.getEndDate()) + 1
                ));
    }

    private Map<Long, Set<Long>> buildPredecessorMap(List<Task> tasks, List<TaskDependency> dependencies) {
        Map<Long, Set<Long>> map = new HashMap<>();
        tasks.forEach(t -> map.put(t.getId(), new HashSet<>()));

        dependencies.forEach(dep ->
                map.get(dep.getTask().getId()).add(dep.getDependsOnTask().getId())
        );

        return map;
    }

    private Map<Long, Set<Long>> buildSuccessorMap(List<Task> tasks, List<TaskDependency> dependencies) {
        Map<Long, Set<Long>> map = new HashMap<>();
        tasks.forEach(t -> map.put(t.getId(), new HashSet<>()));

        dependencies.forEach(dep ->
                map.get(dep.getDependsOnTask().getId()).add(dep.getTask().getId())
        );

        return map;
    }

    private boolean hasCycle(List<Task> tasks, List<TaskDependency> dependencies) {
        Map<Long, Set<Long>> graph = new HashMap<>();
        tasks.forEach(t -> graph.put(t.getId(), new HashSet<>()));

        dependencies.forEach(dep ->
                graph.get(dep.getDependsOnTask().getId()).add(dep.getTask().getId())
        );

        Set<Long> visited = new HashSet<>();
        Set<Long> recursionStack = new HashSet<>();

        for (Task task : tasks) {
            if (!visited.contains(task.getId())) {
                if (hasCycleDFS(task.getId(), graph, visited, recursionStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasCycleDFS(Long nodeId, Map<Long, Set<Long>> graph,
                               Set<Long> visited, Set<Long> recursionStack) {
        visited.add(nodeId);
        recursionStack.add(nodeId);

        for (Long neighbor : graph.getOrDefault(nodeId, new HashSet<>())) {
            if (!visited.contains(neighbor)) {
                if (hasCycleDFS(neighbor, graph, visited, recursionStack)) {
                    return true;
                }
            } else if (recursionStack.contains(neighbor)) {
                return true;
            }
        }

        recursionStack.remove(nodeId);
        return false;
    }
}
