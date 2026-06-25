package com.ganttpro.repository;

import com.ganttpro.model.Task;
import com.ganttpro.model.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
    List<TaskActivity> findByTaskOrderByCreatedAtDesc(Task task);
}
