package com.ganttpro.service;

import com.ganttpro.model.*;
import com.ganttpro.repository.ProjectMemberRepository;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.TaskRepository;
import com.ganttpro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectMemberServiceTests {

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User owner;
    private User member;
    private Project project;

    @BeforeEach
    void setUp() {
        taskDependencyRepository.deleteAll();
        projectMemberRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User("Owner", "owner@test.com", "password");
        member = new User("Member", "member@test.com", "password");
        userRepository.save(owner);
        userRepository.save(member);

        project = new Project("Test Project", "Description", LocalDate.now(), LocalDate.now().plusDays(30), owner);
        projectRepository.save(project);
    }

    @Test
    void testAddMember() throws Exception {
        projectMemberService.addMember(project.getId(), member.getEmail(), ProjectRole.EDITOR, owner);
        Optional<ProjectMember> memberOpt = projectMemberRepository.findByProjectAndUser(project, member);
        assertTrue(memberOpt.isPresent());
        assertEquals(ProjectRole.EDITOR, memberOpt.get().getRole());
    }

    @Test
    void testAddMemberSelfFails() throws Exception {
        User user = new User("User", "user@test.com", "password");
        userRepository.save(user);
        project.setOwner(user);
        projectRepository.save(project);

        assertThrows(Exception.class, () ->
                projectMemberService.addMember(project.getId(), user.getEmail(), ProjectRole.EDITOR, user)
        );
    }

    @Test
    void testAddMemberNotOwnerFails() throws Exception {
        User nonOwner = new User("NonOwner", "nonowner@test.com", "password");
        userRepository.save(nonOwner);

        assertThrows(Exception.class, () ->
                projectMemberService.addMember(project.getId(), member.getEmail(), ProjectRole.EDITOR, nonOwner)
        );
    }

    @Test
    void testUpdateRole() throws Exception {
        projectMemberService.addMember(project.getId(), member.getEmail(), ProjectRole.VIEWER, owner);
        Optional<ProjectMember> memberOpt = projectMemberRepository.findByProjectAndUser(project, member);
        assertTrue(memberOpt.isPresent());

        projectMemberService.updateRole(project.getId(), memberOpt.get().getId(), ProjectRole.EDITOR, owner);
        memberOpt = projectMemberRepository.findByProjectAndUser(project, member);
        assertEquals(ProjectRole.EDITOR, memberOpt.get().getRole());
    }

    @Test
    void testRemoveMember() throws Exception {
        projectMemberService.addMember(project.getId(), member.getEmail(), ProjectRole.VIEWER, owner);
        Optional<ProjectMember> memberOpt = projectMemberRepository.findByProjectAndUser(project, member);
        assertTrue(memberOpt.isPresent());

        projectMemberService.removeMember(project.getId(), memberOpt.get().getId(), owner);
        memberOpt = projectMemberRepository.findByProjectAndUser(project, member);
        assertTrue(memberOpt.isEmpty());
    }

    @Test
    void testIsOwner() {
        assertTrue(projectMemberService.isOwner(project, owner));
        assertFalse(projectMemberService.isOwner(project, member));
    }

    @Test
    void testCanEdit() throws Exception {
        assertFalse(projectMemberService.canEdit(project, member));
        projectMemberService.addMember(project.getId(), member.getEmail(), ProjectRole.EDITOR, owner);
        assertTrue(projectMemberService.canEdit(project, member));
    }

    @Test
    void testCanView() throws Exception {
        assertFalse(projectMemberService.canView(project, member));
        projectMemberService.addMember(project.getId(), member.getEmail(), ProjectRole.VIEWER, owner);
        assertTrue(projectMemberService.canView(project, member));
    }
}
