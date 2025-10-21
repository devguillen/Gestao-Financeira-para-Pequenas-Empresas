package com.gestao.financas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.controller.AuthController;
import com.gestao.financas.dto.LoginDTO;
import com.gestao.financas.entity.User;
import com.gestao.financas.service.PasswordService;
import com.gestao.financas.service.UserService;
import com.gestao.financas.service.VerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordService passwordService;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private AuthenticationManager authManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@test.com");
    }

    @Test
    void register_returnsCreatedUser() throws Exception {
        Mockito.when(userService.register(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_success() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password");

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        Mockito.when(auth.getName()).thenReturn("testuser");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login efetuado com sucesso para: testuser"));
    }

    @Test
    void forgotPassword_callsService() throws Exception {
        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email de redefinição enviado"));

        Mockito.verify(passwordService).createPasswordResetToken("test@test.com");
    }

    @Test
    void resetPassword_callsService() throws Exception {
        mockMvc.perform(post("/auth/reset-password")
                        .param("token", "abc123")
                        .param("newPassword", "newpass"))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha redefinida com sucesso"));

        Mockito.verify(passwordService).resetPassword("abc123", "newpass");
    }

    @Test
    void verifyEmail_success() throws Exception {
        Mockito.when(verificationService.verifyUser("valid-token")).thenReturn(true);

        mockMvc.perform(get("/auth/verify")
                        .param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Conta verificada com sucesso!"));
    }

    @Test
    void verifyEmail_invalidToken() throws Exception {
        Mockito.when(verificationService.verifyUser("invalid")).thenReturn(false);

        mockMvc.perform(get("/auth/verify")
                        .param("token", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token inválido ou expirado."));
    }
}
