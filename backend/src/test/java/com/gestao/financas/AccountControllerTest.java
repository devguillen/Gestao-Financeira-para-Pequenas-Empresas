package com.gestao.financas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.controller.AccountController;
import com.gestao.financas.entity.Account;
import com.gestao.financas.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        account1 = new Account();
        account1.setId(1L);
        account1.setUserId(1L);
        account1.setName("Conta 1");
        account1.setBalance(BigDecimal.valueOf(1000));

        account2 = new Account();
        account2.setId(2L);
        account2.setUserId(1L);
        account2.setName("Conta 2");
        account2.setBalance(BigDecimal.valueOf(500));
    }

    @Test
    void createAccount_returnsCreatedAccount() throws Exception {
        Mockito.when(accountService.createAccount(any(Account.class))).thenReturn(account1);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(account1.getId()))
                .andExpect(jsonPath("$.name").value(account1.getName()))
                .andExpect(jsonPath("$.balance").value(account1.getBalance()));
    }

    @Test
    void getAccountsByUser_returnsList() throws Exception {
        List<Account> accounts = Arrays.asList(account1, account2);
        Mockito.when(accountService.getAccountsByUser(1L)).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(accounts.size()))
                .andExpect(jsonPath("$[0].id").value(account1.getId()))
                .andExpect(jsonPath("$[1].id").value(account2.getId()));
    }

    @Test
    void updateAccount_returnsUpdatedAccount() throws Exception {
        Account updated = new Account();
        updated.setId(1L);
        updated.setName("Conta Atualizada");
        updated.setBalance(BigDecimal.valueOf(1500));

        Mockito.when(accountService.updateAccount(eq(1L), any(Account.class))).thenReturn(updated);

        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Conta Atualizada"))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void deleteAccount_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(accountService).deleteAccount(1L);
    }

    @Test
    void transfer_returnsSuccessMessage() throws Exception {
        Mockito.doNothing().when(accountService).transfer(1L, 2L, BigDecimal.valueOf(200));

        mockMvc.perform(post("/api/accounts/transfer")
                        .param("fromAccountId", "1")
                        .param("toAccountId", "2")
                        .param("amount", "200"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transferência realizada com sucesso"));
    }
}

