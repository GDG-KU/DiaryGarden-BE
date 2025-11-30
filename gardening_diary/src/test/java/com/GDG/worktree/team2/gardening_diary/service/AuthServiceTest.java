package com.GDG.worktree.team2.gardening_diary.service;

import com.GDG.worktree.team2.gardening_diary.dto.AuthResponse;
import com.GDG.worktree.team2.gardening_diary.dto.LoginRequest;
import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Focused unit tests for {@link AuthService#login(LoginRequest)} covering recent password checks.
 */
class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    void loginFailsWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("tester", "wrong");
        User storedUser = new User();
        storedUser.setUsername("tester");
        storedUser.setPassword(passwordEncoder.encode("secret"));

        when(userRepository.findByUsername("tester")).thenReturn(storedUser);

        AuthResponse response = authService.login(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("비밀번호");
    }

    @Test
    void loginSucceedsWhenPasswordMatches() {
        LoginRequest request = new LoginRequest("tester", "secret");
        User storedUser = new User();
        storedUser.setUsername("tester");
        storedUser.setUid("uid-123");
        storedUser.setDisplayName("Tester");
        storedUser.setPassword(passwordEncoder.encode("secret"));

        when(userRepository.findByUsername("tester")).thenReturn(storedUser);

        AuthResponse response = authService.login(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUid()).isEqualTo("uid-123");
        assertThat(response.getUsername()).isEqualTo("tester");
    }
}
