package com.gestao.financas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.gestao.financas.security.CryptoUtil; // Importa a classe CryptoUtil

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email; // será criptografado no banco

    // ===== Verificação de e-mail =====
    @Column(nullable = false)
    private boolean enabled = false;

    // ===== MFA =====
    @Column(nullable = false)
    private boolean mfaEnabled = false;

    @Column(length = 100)
    private String mfaSecret;

    // ===== Bloqueio de conta =====
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column
    private Long lockTime; // timestamp em ms para desbloqueio automático

    // ===== Getters e Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ===== Criptografia no e-mail =====
    public String getEmail() {
        return email != null ? CryptoUtil.decrypt(email) : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? CryptoUtil.encrypt(email) : null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Long getLockTime() {
        return lockTime;
    }

    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }
}
