package com.gestao.financas;


import com.gestao.financas.service.MfaService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MfaServiceTest {

    private GoogleAuthenticator gAuth;
    private MfaService mfaService;

    @BeforeEach
    void setUp() {
        gAuth = mock(GoogleAuthenticator.class);

        // Criamos uma subclasse anônima para injetar o mock, já que o campo gAuth é final
        mfaService = new MfaService() {
            @Override
            public String generateSecretKey() {
                GoogleAuthenticatorKey key = mock(GoogleAuthenticatorKey.class);
                when(key.getKey()).thenReturn("SECRET123");
                when(gAuth.createCredentials()).thenReturn(key);
                return key.getKey();
            }

            @Override
            public boolean verifyCode(String secretKey, int code) {
                return gAuth.authorize(secretKey, code);
            }
        };
    }

    @Test
    void generateSecretKey_returnsKey() {
        String key = mfaService.generateSecretKey();
        assertNotNull(key);
        assertEquals("SECRET123", key);
    }

    @Test
    void verifyCode_returnsTrueIfValid() {
        when(gAuth.authorize("SECRET123", 123456)).thenReturn(true);
        boolean result = mfaService.verifyCode("SECRET123", 123456);
        assertTrue(result);
    }

    @Test
    void verifyCode_returnsFalseIfInvalid() {
        when(gAuth.authorize("SECRET123", 654321)).thenReturn(false);
        boolean result = mfaService.verifyCode("SECRET123", 654321);
        assertFalse(result);
    }
}

