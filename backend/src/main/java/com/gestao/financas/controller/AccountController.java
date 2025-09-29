package com.gestao.financas.controller;

import com.gestao.financas.entity.Account;
import com.gestao.financas.entity.User;
import com.gestao.financas.service.AccountService;
import com.gestao.financas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService; // Para associar conta ao usuário

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody @Valid Account account,
                                                 @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        account.setUser(user);
        Account savedAccount = accountService.createAccount(account);
        return ResponseEntity.ok(savedAccount);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<Account> accounts = accountService.getAccountsByUser(user);
        return ResponseEntity.ok(accounts);
    }
}
