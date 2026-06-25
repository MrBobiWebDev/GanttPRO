package com.ganttpro.repository;

import com.ganttpro.model.ProjectTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTemplateRepository extends JpaRepository<ProjectTemplate, Long> {
    List<ProjectTemplate> findByCategory(String category);
    Optional<ProjectTemplate> findByName(String name);
}
