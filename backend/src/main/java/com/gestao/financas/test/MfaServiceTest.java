package com.gestao.financas.test;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MfaServiceTest {

    @Mock
    private GoogleAuthenticator gAuth;

    @InjectMocks
    private MfaService mfaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateSecretKey() {
        GoogleAuthenticatorKey mockKey = mock(GoogleAuthenticatorKey.class);
        when(mockKey.getKey()).thenReturn("SECRET123");
        when(gAuth.createCredentials()).thenReturn(mockKey);

        String secret = mfaService.generateSecretKey();

        assertNotNull(secret);
        assertEquals("SECRET123", secret);
        verify(gAuth, times(1)).createCredentials();
    }

    @Test
    void testVerifyCode() {
        String secret = "SECRET123";
        int code = 123456;

        when(gAuth.authorize(secret, code)).thenReturn(true);

        boolean result = mfaService.verifyCode(secret, code);

        assertTrue(result);
        verify(gAuth, times(1)).authorize(secret, code);
    }
}
