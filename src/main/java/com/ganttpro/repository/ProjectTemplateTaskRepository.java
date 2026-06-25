package com.ganttpro.repository;

import com.ganttpro.model.ProjectTemplate;
import com.ganttpro.model.ProjectTemplateTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTemplateTaskRepository extends JpaRepository<ProjectTemplateTask, Long> {
    List<ProjectTemplateTask> findByTemplate(ProjectTemplate template);
    List<ProjectTemplateTask> findByTemplateOrderByOrderIndexAsc(ProjectTemplate template);
}
