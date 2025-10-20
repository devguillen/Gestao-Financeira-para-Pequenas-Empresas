package com.gestao.financas.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gestao.financas.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // 🔹 Adicionado para o processo de autenticação JWT (via e-mail)
    Optional<User> findByEmail(String email);

    // 🔹 (Opcional) útil no registro de novos usuários
    boolean existsByEmail(String email);
}
