package com.ganttpro.repository;

import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByProjectOrderByStartDateAsc(Project project);
    Optional<Task> findByIdAndProject(Long id, Project project);
    List<Task> findByProjectAndParentTaskIsNullOrderByStartDateAsc(Project project);
    List<Task> findByParentTaskOrderByStartDateAsc(Task parentTask);
}
