package com.ganttpro.service;

import com.ganttpro.dto.RegisterRequest;
import com.ganttpro.model.User;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @BeforeEach
    void setUp() {
        taskDependencyRepository.deleteAll();
        projectMemberRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123",
                "password123"
        );

        boolean result = authService.register(request);

        assertTrue(result);
        Optional<User> user = authService.findByEmail("test@example.com");
        assertTrue(user.isPresent());
        assertEquals("Test User", user.get().getName());
    }

    @Test
    void testRegisterWithDuplicateEmail() {
        RegisterRequest request1 = new RegisterRequest(
                "User 1",
                "duplicate@example.com",
                "password123",
                "password123"
        );

        RegisterRequest request2 = new RegisterRequest(
                "User 2",
                "duplicate@example.com",
                "password123",
                "password123"
        );

        authService.register(request1);
        boolean result = authService.register(request2);

        assertFalse(result);
    }

    @Test
    void testRegisterWithMismatchedPasswords() {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123",
                "password456"
        );

        boolean result = authService.register(request);

        assertFalse(result);
    }

    @Test
    void testValidatePassword() {
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        boolean isValid = authService.validatePassword(rawPassword, encodedPassword);

        assertTrue(isValid);
    }

    @Test
    void testValidatePasswordWithInvalidPassword() {
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        boolean isValid = authService.validatePassword("wrongPassword", encodedPassword);

        assertFalse(isValid);
    }
}
