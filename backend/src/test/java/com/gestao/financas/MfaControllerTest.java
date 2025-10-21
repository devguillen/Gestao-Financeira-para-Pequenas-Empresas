package com.gestao.financas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.controller.MfaController;
import com.gestao.financas.dto.EnableMfaRequest;
import com.gestao.financas.dto.VerifyMfaRequest;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MfaController.class)
class MfaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user@test.com");
        user.setMfaEnabled(false);
    }

    @Test
    void enableMfa_success() throws Exception {
        EnableMfaRequest request = new EnableMfaRequest();
        request.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/mfa/enable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("MFA ativada com sucesso")));
    }

    @Test
    void enableMfa_userNotFound() throws Exception {
        EnableMfaRequest request = new EnableMfaRequest();
        request.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/mfa/enable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário não encontrado"));
    }

    @Test
    void verifyMfa_success() throws Exception {
        user.setMfaEnabled(true);
        user.setMfaSecret("SECRET"); // use qualquer string válida

        VerifyMfaRequest request = new VerifyMfaRequest();
        request.setUserId(1L);
        request.setCode(123456); // código simulado

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/mfa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Código MFA validado com sucesso")));
    }

    @Test
    void verifyMfa_userNotFound() throws Exception {
        VerifyMfaRequest request = new VerifyMfaRequest();
        request.setUserId(99L);
        request.setCode(123456);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/mfa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário não encontrado"));
    }

    @Test
    void verifyMfa_notEnabled() throws Exception {
        VerifyMfaRequest request = new VerifyMfaRequest();
        request.setUserId(1L);
        request.setCode(123456);

        user.setMfaEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/mfa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("MFA não está ativada para este usuário"));
    }
}
