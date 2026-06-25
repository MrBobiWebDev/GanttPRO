package com.ganttpro.repository;

import com.ganttpro.model.Project;
import com.ganttpro.model.Task;
import com.ganttpro.model.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {
    List<TaskDependency> findByTaskAndProject(Task task, Project project);
    List<TaskDependency> findByDependsOnTaskAndProject(Task dependsOnTask, Project project);
    List<TaskDependency> findByProject(Project project);
    Optional<TaskDependency> findByTaskAndDependsOnTaskAndProject(Task task, Task dependsOnTask, Project project);
}
