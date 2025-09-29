package com.gestao.financas.controller;

import com.gestao.financas.dto.EnableMfaRequest;
import com.gestao.financas.dto.VerifyMfaRequest;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    @Autowired
    private UserRepository userRepository;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    // ===== Habilitar MFA =====
    @PostMapping("/enable")
    public ResponseEntity<?> enableMfa(@Valid @RequestBody EnableMfaRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        User user = optionalUser.get();

        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();

        user.setMfaEnabled(true);
        user.setMfaSecret(secret);
        userRepository.save(user);

        return ResponseEntity.ok(
            "MFA ativada com sucesso. Guarde esta chave para configurar seu app TOTP: " + secret
        );
    }

    // ===== Verificar código MFA =====
    @PostMapping("/verify")
    public ResponseEntity<?> verifyMfa(@Valid @RequestBody VerifyMfaRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        User user = optionalUser.get();

        if (!user.isMfaEnabled() || user.getMfaSecret() == null) {
            return ResponseEntity.badRequest().body("MFA não está ativada para este usuário");
        }

        boolean isCodeValid = gAuth.authorize(user.getMfaSecret(), request.getCode());

        if (!isCodeValid) {
            return ResponseEntity.status(401).body("Código MFA inválido");
        }

        return ResponseEntity.ok("Código MFA validado com sucesso");
    }
}