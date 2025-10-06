package com.gestao.financas.test;

import com.gestao.financas.entity.PasswordResetToken;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.PasswordResetTokenRepository;
import com.gestao.financas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private PasswordService passwordService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("user@test.com");
        user.setEmail("user@test.com");
    }

    @Test
    void testCreatePasswordResetToken() {
        when(userRepository.findByUsername("user@test.com")).thenReturn(Optional.of(user));

        passwordService.createPasswordResetToken("user@test.com");

        // Verifica que o token foi salvo
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));

        // Verifica que o e-mail foi enviado
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetPassword() {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setToken("TOKEN123");

        when(tokenRepository.findByToken("TOKEN123")).thenReturn(Optional.of(token));
        when(encoder.encode("newPassword")).thenReturn("ENCODED_PASS");

        passwordService.resetPassword("TOKEN123", "newPassword");

        // Verifica que a senha foi atualizada
        assertEquals("ENCODED_PASS", user.getPassword());

        // Verifica que o usuário foi salvo
        verify(userRepository, times(1)).save(user);

        // Verifica que o token foi removido
        verify(tokenRepository, times(1)).delete(token);
    }

    @Test
    void testResetPasswordTokenExpired() {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        token.setToken("TOKEN123");

        when(tokenRepository.findByToken("TOKEN123")).thenReturn(Optional.of(token));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordService.resetPassword("TOKEN123", "newPassword"));

        assertEquals("Token expirado", ex.getMessage());
    }

    @Test
    void testCreatePasswordResetTokenUserNotFound() {
        when(userRepository.findByUsername("notfound@test.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordService.createPasswordResetToken("notfound@test.com"));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void testResetPasswordInvalidToken() {
        when(tokenRepository.findByToken("INVALID")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordService.resetPassword("INVALID", "newPassword"));

        assertEquals("Token inválido", ex.getMessage());
    }
}
