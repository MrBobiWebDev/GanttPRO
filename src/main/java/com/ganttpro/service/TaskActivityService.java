package com.ganttpro.service;

import com.ganttpro.model.Task;
import com.ganttpro.model.TaskActivity;
import com.ganttpro.model.TaskActivityType;
import com.ganttpro.model.User;
import com.ganttpro.repository.TaskActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskActivityService {
    private final TaskActivityRepository activityRepository;

    public TaskActivityService(TaskActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public TaskActivity logActivity(Task task, User actor, TaskActivityType type, String description) {
        TaskActivity activity = new TaskActivity(task, actor, type, description);
        return activityRepository.save(activity);
    }

    public List<TaskActivity> getActivities(Task task) {
        return activityRepository.findByTaskOrderByCreatedAtDesc(task);
    }
}
