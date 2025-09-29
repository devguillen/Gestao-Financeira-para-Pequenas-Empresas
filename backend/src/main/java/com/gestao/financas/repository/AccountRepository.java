package com.gestao.financas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestao.financas.entity.Account;
import com.gestao.financas.entity.User;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
