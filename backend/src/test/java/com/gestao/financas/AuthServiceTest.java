package com.gestao.financas;

import com.gestao.financas.entity.User;
import com.gestao.financas.repository.UserRepository;
import com.gestao.financas.service.AuthService;
import com.gestao.financas.service.SecurityLogService;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private SecurityLogService securityLogService;
    private HttpServletRequest request;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        securityLogService = mock(SecurityLogService.class);
        request = mock(HttpServletRequest.class);

        // ✅ Criando o serviço via construtor com os mocks
        authService = new AuthService(userRepository, passwordEncoder, securityLogService, request);
    }

    @Test
    void login_successful() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("encodedPass");
        user.setFailedLoginAttempts(2);

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String token = authService.login("test", "pass");

        assertNotNull(token);
        assertEquals(0, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
        verify(securityLogService).logEvent(1L, "LOGIN_SUCCESS", "127.0.0.1");
    }

    @Test
    void login_wrongPassword_incrementsFailedAttempts() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("encodedPass");
        user.setFailedLoginAttempts(2);

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login("test", "wrong"));

        assertEquals("Senha incorreta", exception.getMessage());
        assertEquals(3, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
        verify(securityLogService).logEvent(1L, "LOGIN_FAILURE", "127.0.0.1");
    }

    @Test
    void login_accountLocked_throwsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("encodedPass");
        user.setLockTime(System.currentTimeMillis() + 60000); // 1 min bloqueado

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login("test", "pass"));

        assertTrue(exception.getMessage().contains("Conta bloqueada"));
        verify(securityLogService).logEvent(1L, "LOGIN_FAILURE_BLOCKED", "127.0.0.1");
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_accountUnlocksAfterLockTime() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("encodedPass");
        user.setLockTime(System.currentTimeMillis() - 1000); // bloqueio expirado
        user.setFailedLoginAttempts(4);

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String token = authService.login("test", "pass");

        assertNotNull(token);
        assertNull(user.getLockTime());
        assertEquals(0, user.getFailedLoginAttempts());
        verify(userRepository, times(2)).save(user); // 1 para desbloqueio + 1 para login
        verify(securityLogService).logEvent(1L, "LOGIN_SUCCESS", "127.0.0.1");
    }

    @Test
    void login_userNotFound_throwsException() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login("test", "pass"));

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }
}
