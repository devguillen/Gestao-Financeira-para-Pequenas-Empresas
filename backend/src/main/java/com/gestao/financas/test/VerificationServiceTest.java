package com.gestao.financas.service;

import com.gestao.financas.entity.User;
import com.gestao.financas.entity.VerificationToken;
import com.gestao.financas.repository.UserRepository;
import com.gestao.financas.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationServiceTest {

    @Mock
    private VerificationTokenRepository tokenRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private VerificationService verificationService;

    private User user;
    private VerificationToken token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("gabriel");
        user.setEnabled(false);

        token = new VerificationToken();
        token.setUser(user);
        token.setToken("abc123");
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
    }

    @Test
    void testCreateToken() {
        when(tokenRepo.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VerificationToken savedToken = verificationService.createToken(user);

        assertNotNull(savedToken);
        assertEquals(user, savedToken.getUser());
        verify(tokenRepo, times(1)).save(any(VerificationToken.class));
    }

    @Test
    void testVerifyUserValidToken() {
        when(tokenRepo.findByToken("abc123")).thenReturn(Optional.of(token));

        boolean result = verificationService.verifyUser("abc123");

        assertTrue(result);
        assertTrue(user.isEnabled());
        verify(userRepo, times(1)).save(user);
        verify(tokenRepo, times(1)).delete(token);
    }

    @Test
    void testVerifyUserExpiredToken() {
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepo.findByToken("abc123")).thenReturn(Optional.of(token));

        boolean result = verificationService.verifyUser("abc123");

        assertFalse(result);
        assertFalse(user.isEnabled());
        verify(userRepo, never()).save(any());
        verify(tokenRepo, never()).delete(any());
    }

    @Test
    void testVerifyUserTokenNotFound() {
        when(tokenRepo.findByToken("invalid")).thenReturn(Optional.empty());

        boolean result = verificationService.verifyUser("invalid");

        assertFalse(result);
        verify(userRepo, never()).save(any());
        verify(tokenRepo, never()).delete(any());
    }
}
