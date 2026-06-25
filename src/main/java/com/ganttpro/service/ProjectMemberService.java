package com.ganttpro.service;

import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectMemberRepository;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                              ProjectRepository projectRepository,
                              UserRepository userRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectMember addMember(Long projectId, String userEmail, ProjectRole role, User currentUser) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        if (!isOwner(project, currentUser)) {
            throw new Exception("Только владелец может добавлять участников");
        }

        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new Exception("Пользователь с email " + userEmail + " не найден");
        }

        User newMember = userOpt.get();
        if (newMember.getId().equals(currentUser.getId())) {
            throw new Exception("Вы уже владелец проекта");
        }

        Optional<ProjectMember> existingMember = projectMemberRepository.findByProjectAndUser(project, newMember);
        if (existingMember.isPresent()) {
            throw new Exception("Пользователь уже участник проекта");
        }

        ProjectMember member = new ProjectMember(project, newMember, role);
        return projectMemberRepository.save(member);
    }

    public ProjectMember updateRole(Long projectId, Long memberId, ProjectRole newRole, User currentUser) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        if (!isOwner(project, currentUser)) {
            throw new Exception("Только владелец может менять роли");
        }

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new Exception("Участник не найден"));

        if (!member.getProject().getId().equals(projectId)) {
            throw new Exception("Участник не входит в этот проект");
        }

        member.setRole(newRole);
        return projectMemberRepository.save(member);
    }

    public void removeMember(Long projectId, Long memberId, User currentUser) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));

        if (!isOwner(project, currentUser)) {
            throw new Exception("Только владелец может удалять участников");
        }

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new Exception("Участник не найден"));

        if (!member.getProject().getId().equals(projectId)) {
            throw new Exception("Участник не входит в этот проект");
        }

        projectMemberRepository.delete(member);
    }

    public List<ProjectMember> getProjectMembers(Long projectId) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Проект не найден"));
        return projectMemberRepository.findByProject(project);
    }

    public List<ProjectMember> getUserProjects(User user) {
        return projectMemberRepository.findByUser(user);
    }

    public Optional<ProjectMember> getMember(Project project, User user) {
        return projectMemberRepository.findByProjectAndUser(project, user);
    }

    public boolean isOwner(Project project, User user) {
        return project.getOwner().getId().equals(user.getId());
    }

    public boolean isMember(Project project, User user) {
        Optional<ProjectMember> member = projectMemberRepository.findByProjectAndUser(project, user);
        return member.isPresent();
    }

    public boolean canEdit(Project project, User user) {
        if (isOwner(project, user)) {
            return true;
        }
        Optional<ProjectMember> member = projectMemberRepository.findByProjectAndUser(project, user);
        return member.isPresent() && (member.get().getRole() == ProjectRole.EDITOR || member.get().getRole() == ProjectRole.OWNER);
    }

    public boolean canView(Project project, User user) {
        if (isOwner(project, user)) {
            return true;
        }
        return projectMemberRepository.findByProjectAndUser(project, user).isPresent();
    }

    public boolean hasAccess(Project project, User user) {
        return canView(project, user);
    }
}
