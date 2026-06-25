package com.ganttpro.repository;

import com.ganttpro.model.Project;
import com.ganttpro.model.ProjectMember;
import com.ganttpro.model.ProjectRole;
import com.ganttpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);
    List<ProjectMember> findByUser(User user);
    Optional<ProjectMember> findByProjectAndUser(Project project, User user);
    List<ProjectMember> findByProjectAndRole(Project project, ProjectRole role);
}
