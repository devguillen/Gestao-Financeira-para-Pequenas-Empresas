package com.gestao.financas;

import com.gestao.financas.entity.PasswordResetToken;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.PasswordResetTokenRepository;
import com.gestao.financas.repository.UserRepository;
import com.gestao.financas.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordServiceTest {

    private UserRepository userRepository;
    private PasswordResetTokenRepository tokenRepository;
    private JavaMailSender mailSender;
    private PasswordEncoder encoder;
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(PasswordResetTokenRepository.class);
        mailSender = mock(JavaMailSender.class);
        encoder = mock(PasswordEncoder.class);

        // Injeta mocks via construtor
        passwordService = new PasswordService(userRepository, tokenRepository, mailSender, encoder);
    }

    @Test
void createPasswordResetToken_sendsEmailAndSavesToken() {
    User user = new User();
    user.setId(1L);
    user.setEmail("user@test.com");
    user.setUsername("user@test.com");

    when(userRepository.findByUsername("user@test.com")).thenReturn(Optional.of(user));
    when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    passwordService.createPasswordResetToken("user@test.com");

    ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
    verify(tokenRepository).save(tokenCaptor.capture());
    PasswordResetToken savedToken = tokenCaptor.getValue();

    assertNotNull(savedToken.getToken());
    assertEquals(user, savedToken.getUser());

    ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(mailCaptor.capture());
    SimpleMailMessage message = mailCaptor.getValue();

    // Verificações adicionais para evitar null pointer
    assertNotNull(message, "O SimpleMailMessage não deve ser nulo");
    assertNotNull(message.getTo(), "O campo 'to' não deve ser nulo");
    assertTrue(message.getTo().length > 0, "O campo 'to' deve ter ao menos um destinatário");
    assertEquals("user@test.com", message.getTo()[0]);

    assertNotNull(message.getText(), "O texto do e-mail não deve ser nulo");
    assertTrue(message.getText().contains(savedToken.getToken()), "O e-mail deve conter o token");
}

    @Test
    void resetPassword_successful() {
        User user = new User();
        user.setId(1L);
        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(prt));
        when(encoder.encode("newpass")).thenReturn("encodedPass");

        passwordService.resetPassword("valid-token", "newpass");

        assertEquals("encodedPass", user.getPassword());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(prt);
    }

    @Test
    void resetPassword_tokenExpired_throwsException() {
        PasswordResetToken prt = new PasswordResetToken();
        prt.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(prt));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                passwordService.resetPassword("expired-token", "pass"));

        assertEquals("Token expirado", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void resetPassword_tokenInvalid_throwsException() {
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                passwordService.resetPassword("invalid-token", "pass"));

        assertEquals("Token inválido", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }
}
