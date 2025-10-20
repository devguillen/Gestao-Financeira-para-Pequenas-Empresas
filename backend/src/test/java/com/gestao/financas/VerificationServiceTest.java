package com.gestao.financas;

import com.gestao.financas.entity.User;
import com.gestao.financas.entity.VerificationToken;
import com.gestao.financas.repository.UserRepository;
import com.gestao.financas.repository.VerificationTokenRepository;
import com.gestao.financas.service.VerificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationServiceTest {

    private VerificationTokenRepository tokenRepo;
    private UserRepository userRepo;
    private VerificationService verificationService;

    @BeforeEach
    void setUp() {
        tokenRepo = mock(VerificationTokenRepository.class);
        userRepo = mock(UserRepository.class);
        verificationService = new VerificationService(tokenRepo, userRepo);
    }

    @Test
    void createToken_savesToken() {
        User user = new User();
        VerificationToken token = VerificationToken.create(user);

        when(tokenRepo.save(any(VerificationToken.class))).thenReturn(token);

        VerificationToken saved = verificationService.createToken(user);

        assertNotNull(saved);
        assertEquals(user, saved.getUser());
        verify(tokenRepo).save(any(VerificationToken.class));
    }

    @Test
    void verifyUser_validToken_enablesUserAndDeletesToken() {
        User user = new User();
        user.setEnabled(false);
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(tokenRepo.findByToken("valid")).thenReturn(Optional.of(token));

        boolean result = verificationService.verifyUser("valid");

        assertTrue(result);
        assertTrue(user.isEnabled());
        verify(userRepo).save(user);
        verify(tokenRepo).delete(token);
    }

    @Test
    void verifyUser_expiredToken_returnsFalse() {
        VerificationToken token = new VerificationToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(5));

        when(tokenRepo.findByToken("expired")).thenReturn(Optional.of(token));

        boolean result = verificationService.verifyUser("expired");

        assertFalse(result);
        verify(userRepo, never()).save(any());
        verify(tokenRepo, never()).delete(any());
    }

    @Test
    void verifyUser_nonExistentToken_returnsFalse() {
        when(tokenRepo.findByToken("missing")).thenReturn(Optional.empty());

        boolean result = verificationService.verifyUser("missing");

        assertFalse(result);
        verify(userRepo, never()).save(any());
        verify(tokenRepo, never()).delete(any());
    }
}

