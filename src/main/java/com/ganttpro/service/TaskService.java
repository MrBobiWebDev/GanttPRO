package com.ganttpro.service;

import com.ganttpro.dto.TaskForm;
import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectMemberService projectMemberService;
    private final ProjectRepository projectRepository;
    private final TaskActivityService activityService;
    private final EntityManager entityManager;

    public TaskService(TaskRepository taskRepository, ProjectMemberService projectMemberService,
                      ProjectRepository projectRepository, TaskActivityService activityService,
                      EntityManager entityManager) {
        this.taskRepository = taskRepository;
        this.projectMemberService = projectMemberService;
        this.projectRepository = projectRepository;
        this.activityService = activityService;
        this.entityManager = entityManager;
    }

    public List<Task> getProjectTasks(Project project) {
        return taskRepository.findByProjectAndParentTaskIsNullOrderByStartDateAsc(project);
    }

    public List<Task> getSubtasks(Task parentTask) {
        return taskRepository.findByParentTaskOrderByStartDateAsc(parentTask);
    }

    public Optional<Task> getTask(Long id, Project project) {
        return taskRepository.findByIdAndProject(id, project);
    }

    @Transactional
    public Task createTask(Project project, TaskForm form, User user) throws Exception {
        if (!projectMemberService.canEdit(project, user)) {
            throw new Exception("Только редактор или владелец может создавать задачи");
        }
        Task task = new Task(project, form.getTitle(), form.getDescription(),
                form.getStartDate(), form.getEndDate(), form.getAssigneeName());
        task.setStatus(TaskStatus.TODO);
        task.setProgress(0);
        task.setPriority(form.getPriority() != null ? form.getPriority() : TaskPriority.MEDIUM);

        if (form.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(form.getParentTaskId()).orElse(null);
            if (parentTask != null && parentTask.getProject().getId().equals(project.getId())) {
                task.setParentTask(parentTask);
            }
        }

        Task savedTask = taskRepository.save(task);
        activityService.logActivity(savedTask, user, TaskActivityType.CREATED, "Задача создана");
        return savedTask;
    }

    @Transactional
    public Task updateTask(Task task, TaskForm form, User user) throws Exception {
        System.out.println("=== updateTask called for task ID: " + task.getId() + " ===");
        System.out.println("New status: " + form.getStatus());

        if (!projectMemberService.canEdit(task.getProject(), user)) {
            throw new Exception("Только редактор или владелец может редактировать задачи");
        }

        TaskStatus oldStatus = task.getStatus();
        Integer oldProgress = task.getProgress();
        String oldAssignee = task.getAssigneeName();
        TaskPriority oldPriority = task.getPriority();

        task.setTitle(form.getTitle());
        task.setDescription(form.getDescription());
        task.setStartDate(form.getStartDate());
        task.setEndDate(form.getEndDate());
        task.setStatus(form.getStatus());
        task.setAssigneeName(form.getAssigneeName());
        task.setPriority(form.getPriority() != null ? form.getPriority() : TaskPriority.MEDIUM);
        task.setUpdatedAt(LocalDateTime.now());

        if (form.getStatus() == TaskStatus.DONE) {
            task.setProgress(100);
        } else {
            task.setProgress(form.getProgress());
        }

        if (!oldStatus.equals(form.getStatus())) {
            activityService.logActivity(task, user, TaskActivityType.STATUS_CHANGED,
                String.format("Статус изменён с \"%s\" на \"%s\"", oldStatus.name(), form.getStatus().name()));
        }
        if (!oldProgress.equals(form.getProgress())) {
            activityService.logActivity(task, user, TaskActivityType.PROGRESS_CHANGED,
                String.format("Прогресс изменён с %d%% на %d%%", oldProgress, form.getProgress()));
        }
        if (!oldAssignee.equals(form.getAssigneeName())) {
            activityService.logActivity(task, user, TaskActivityType.ASSIGNEE_CHANGED,
                String.format("Исполнитель изменён с \"%s\" на \"%s\"", oldAssignee, form.getAssigneeName()));
        }
        if (!oldPriority.equals(form.getPriority())) {
            activityService.logActivity(task, user, TaskActivityType.PRIORITY_CHANGED,
                String.format("Приоритет изменён с \"%s\" на \"%s\"", oldPriority.getDisplayName(),
                    form.getPriority().getDisplayName()));
        }

        Task saved = taskRepository.save(task);
        System.out.println("=== Task saved with status: " + saved.getStatus() + " ===");
        return saved;
    }

    @Transactional
    public void deleteTask(Long id, User user) throws Exception {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            throw new Exception("Задача не найдена");
        }

        Project project = projectRepository.findById(task.getProject().getId())
                .orElseThrow(() -> new Exception("Проект не найден"));

        if (!projectMemberService.isMember(project, user) && !projectMemberService.isOwner(project, user)) {
            throw new Exception("У вас нет доступа к этому проекту");
        }

        try {
            entityManager.createNativeQuery("DELETE FROM task_dependencies WHERE task_id = :taskId OR depends_on_task_id = :taskId")
                    .setParameter("taskId", id)
                    .executeUpdate();
            taskRepository.deleteById(id);
        } catch (Exception e) {
            System.err.println("Error deleting task " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Ошибка при удалении задачи: " + e.getMessage());
        }
    }

    public boolean validateDates(TaskForm form) {
        return form.getStartDate() != null && form.getEndDate() != null &&
               !form.getEndDate().isBefore(form.getStartDate());
    }

    public boolean validateProgress(Integer progress) {
        return progress != null && progress >= 0 && progress <= 100;
    }
}
