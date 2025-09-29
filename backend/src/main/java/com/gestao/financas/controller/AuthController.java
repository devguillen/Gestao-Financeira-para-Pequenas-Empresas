package com.gestao.financas.controller;

import com.gestao.financas.dto.LoginDTO;
import com.gestao.financas.entity.User;
import com.gestao.financas.service.PasswordService;
import com.gestao.financas.service.UserService;
import com.gestao.financas.service.VerificationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private VerificationService verificationService; // ✅ injeta o serviço corretamente

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO dto) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        return ResponseEntity.ok("Login efetuado com sucesso para: " + auth.getName());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        passwordService.createPasswordResetToken(email);
        return ResponseEntity.ok("Email de redefinição enviado");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        passwordService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Senha redefinida com sucesso");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean ok = verificationService.verifyUser(token); // ✅ uso de instância
        return ok
            ? ResponseEntity.ok("Conta verificada com sucesso!")
            : ResponseEntity.badRequest().body("Token inválido ou expirado.");
    }
}
