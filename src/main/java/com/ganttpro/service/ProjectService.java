package com.ganttpro.service;

import com.ganttpro.dto.ProjectForm;
import com.ganttpro.model.Project;
import com.ganttpro.model.ProjectMember;
import com.ganttpro.model.User;
import com.ganttpro.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberService projectMemberService;

    public ProjectService(ProjectRepository projectRepository, ProjectMemberService projectMemberService) {
        this.projectRepository = projectRepository;
        this.projectMemberService = projectMemberService;
    }

    public List<Project> getUserProjects(User user) {
        List<Project> ownedProjects = projectRepository.findByOwnerOrderByCreatedAtDesc(user);
        Set<Project> allProjects = new HashSet<>(ownedProjects);

        List<ProjectMember> memberProjects = projectMemberService.getUserProjects(user);
        for (ProjectMember member : memberProjects) {
            allProjects.add(member.getProject());
        }

        return allProjects.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    public Optional<Project> getProject(Long id, User user) {
        Optional<Project> project = projectRepository.findByIdAndOwner(id, user);
        if (project.isPresent()) {
            return project;
        }

        if (projectMemberService.canView(projectRepository.findById(id).orElse(null), user)) {
            return projectRepository.findById(id);
        }

        return Optional.empty();
    }

    public Project createProject(ProjectForm form, User owner) {
        Project project = new Project(form.getName(), form.getDescription(),
                form.getStartDate(), form.getEndDate(), owner);
        return projectRepository.save(project);
    }

    public Project updateProject(Project project, ProjectForm form, User user) throws Exception {
        if (!projectMemberService.isOwner(project, user)) {
            throw new Exception("Только владелец может редактировать проект");
        }
        project.setName(form.getName());
        project.setDescription(form.getDescription());
        project.setStartDate(form.getStartDate());
        project.setEndDate(form.getEndDate());
        return projectRepository.save(project);
    }

    public void deleteProject(Long id, User user) throws Exception {
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            throw new Exception("Проект не найден");
        }

        if (!projectMemberService.isOwner(project, user)) {
            throw new Exception("Только владелец может удалить проект");
        }

        projectRepository.deleteById(id);
    }

    public boolean canEdit(Project project, User user) {
        return projectMemberService.canEdit(project, user);
    }

    public boolean canDelete(Project project, User user) {
        return projectMemberService.isOwner(project, user);
    }

    public boolean canView(Project project, User user) {
        return projectMemberService.canView(project, user);
    }

    public double getProjectProgress(Project project) {
        if (project.getTasks().isEmpty()) {
            return 0.0;
        }
        return project.getTasks().stream()
                .mapToInt(t -> t.getProgress() != null ? t.getProgress() : 0)
                .average()
                .orElse(0.0);
    }

    public long getUserProjectCount(User user) {
        return getUserProjects(user).size();
    }

    public long getActiveTasksCount(User user) {
        return getUserProjects(user).stream()
                .flatMap(p -> p.getTasks().stream())
                .filter(t -> !t.getStatus().name().equals("DONE"))
                .count();
    }
}
