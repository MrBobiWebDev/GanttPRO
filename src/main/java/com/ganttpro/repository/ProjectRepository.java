package com.ganttpro.repository;

import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Project> findByIdAndOwner(Long id, User owner);
}
