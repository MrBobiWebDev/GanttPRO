package com.ganttpro.service;

import com.ganttpro.dto.ProjectForm;
import com.ganttpro.model.Project;
import com.ganttpro.model.User;
import com.ganttpro.model.UserRole;
import com.ganttpro.repository.ProjectMemberRepository;
import com.ganttpro.repository.ProjectRepository;
import com.ganttpro.repository.TaskDependencyRepository;
import com.ganttpro.repository.TaskRepository;
import com.ganttpro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTests {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        taskDependencyRepository.deleteAll();
        projectMemberRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("Test User", "test@example.com",
                passwordEncoder.encode("password123"));
        testUser.setRole(UserRole.USER);
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateProject() {
        ProjectForm form = new ProjectForm(
                "Test Project",
                "Test Description",
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );

        Project project = projectService.createProject(form, testUser);

        assertNotNull(project.getId());
        assertEquals("Test Project", project.getName());
        assertEquals("Test Description", project.getDescription());
        assertEquals(testUser.getId(), project.getOwner().getId());
    }

    @Test
    void testGetUserProjects() {
        ProjectForm form1 = new ProjectForm("Project 1", "Desc 1",
                LocalDate.now(), LocalDate.now().plusDays(10));
        ProjectForm form2 = new ProjectForm("Project 2", "Desc 2",
                LocalDate.now(), LocalDate.now().plusDays(20));

        projectService.createProject(form1, testUser);
        projectService.createProject(form2, testUser);

        List<Project> projects = projectService.getUserProjects(testUser);

        assertEquals(2, projects.size());
    }

    @Test
    void testGetProject() {
        ProjectForm form = new ProjectForm("Test Project", "Description",
                LocalDate.now(), LocalDate.now().plusDays(30));
        Project created = projectService.createProject(form, testUser);

        Optional<Project> found = projectService.getProject(created.getId(), testUser);

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void testGetProjectWithWrongUser() {
        ProjectForm form = new ProjectForm("Test Project", "Description",
                LocalDate.now(), LocalDate.now().plusDays(30));
        Project created = projectService.createProject(form, testUser);

        User otherUser = new User("Other User", "other@example.com",
                passwordEncoder.encode("password123"));
        otherUser = userRepository.save(otherUser);

        Optional<Project> found = projectService.getProject(created.getId(), otherUser);

        assertTrue(found.isEmpty());
    }

    @Test
    void testUpdateProject() throws Exception {
        ProjectForm form = new ProjectForm("Original Name", "Original Description",
                LocalDate.now(), LocalDate.now().plusDays(30));
        Project project = projectService.createProject(form, testUser);

        ProjectForm updatedForm = new ProjectForm("Updated Name", "Updated Description",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(40));
        Project updated = projectService.updateProject(project, updatedForm, testUser);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    void testDeleteProject() throws Exception {
        ProjectForm form = new ProjectForm("Test Project", "Description",
                LocalDate.now(), LocalDate.now().plusDays(30));
        Project project = projectService.createProject(form, testUser);
        Long projectId = project.getId();

        projectService.deleteProject(projectId, testUser);

        Optional<Project> found = projectRepository.findById(projectId);
        assertTrue(found.isEmpty());
    }
}
