package com.gestao.financas.service;

import com.gestao.financas.entity.User;
import com.gestao.financas.entity.VerificationToken;
import com.gestao.financas.repository.UserRepository;
import com.gestao.financas.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepo;

    public VerificationToken createToken(User user) {
        VerificationToken token = VerificationToken.create(user);
        return tokenRepo.save(token);
    }

    public boolean verifyUser(String token) {
        return tokenRepo.findByToken(token).map(vt -> {
            if (vt.getExpiryDate().isAfter(java.time.LocalDateTime.now())) {
                User u = vt.getUser();
                u.setEnabled(true);
                userRepo.save(u);
                tokenRepo.delete(vt);
                return true;
            }
            return false;
        }).orElse(false);
    }
}