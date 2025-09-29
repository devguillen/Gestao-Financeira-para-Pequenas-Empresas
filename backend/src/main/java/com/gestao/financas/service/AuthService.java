package com.gestao.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestao.financas.entity.User;
import com.gestao.financas.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // se estiver usando Spring Security

    // ===== Constantes =====
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 15 * 60 * 1000; // 15 minutos

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verifica se a conta está bloqueada
        if (user.getLockTime() != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime < user.getLockTime()) {
                throw new RuntimeException("Conta bloqueada. Tente novamente mais tarde.");
            } else {
                // Desbloquear conta após tempo
                user.setLockTime(null);
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
        }

        // Verifica senha
        if (!passwordEncoder.matches(password, user.getPassword())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockTime(System.currentTimeMillis() + LOCK_TIME_DURATION);
            }

            userRepository.save(user);
            throw new RuntimeException("Senha incorreta");
        }

        // Login bem-sucedido
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        // Aqui você gera JWT ou sessão
        return "JWT_TOKEN_AQUI";
    }
}
