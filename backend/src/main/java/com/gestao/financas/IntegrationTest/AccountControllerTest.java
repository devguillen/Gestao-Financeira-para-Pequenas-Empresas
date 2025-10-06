package com.gestao.financas.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.entity.Account;
import com.gestao.financas.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        account1 = new Account();
        account1.setName("Conta A");
        account1.setType("Checking");
        account1.setBalance(BigDecimal.valueOf(1000));
        account1.setUserId(1L);
        account1 = accountRepository.save(account1);

        account2 = new Account();
        account2.setName("Conta B");
        account2.setType("Savings");
        account2.setBalance(BigDecimal.valueOf(500));
        account2.setUserId(1L);
        account2 = accountRepository.save(account2);
    }

    @Test
    void testCreateAccount() throws Exception {
        Account newAccount = new Account();
        newAccount.setName("Conta C");
        newAccount.setType("Checking");
        newAccount.setBalance(BigDecimal.valueOf(200));
        newAccount.setUserId(1L);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Conta C"))
                .andExpect(jsonPath("$.balance").value(200));
    }

    @Test
    void testGetAccountsByUser() throws Exception {
        mockMvc.perform(get("/api/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Conta A")))
                .andExpect(jsonPath("$[1].name", is("Conta B")));
    }

    @Test
    void testUpdateAccount() throws Exception {
        account1.setName("Conta A Atualizada");
        account1.setBalance(BigDecimal.valueOf(1500));

        mockMvc.perform(put("/api/accounts/" + account1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Conta A Atualizada"))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/api/accounts/" + account1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testTransferBetweenAccounts() throws Exception {
        mockMvc.perform(post("/api/accounts/transfer")
                .param("fromAccountId", account1.getId().toString())
                .param("toAccountId", account2.getId().toString())
                .param("amount", "300"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transferência realizada com sucesso"));

        // Verifica saldos atualizados
        mockMvc.perform(get("/api/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Conta A')].balance", contains(700)))
                .andExpect(jsonPath("$[?(@.name=='Conta B')].balance", contains(800)));
    }

    @Test
    void testTransferInsufficientFunds() throws Exception {
        mockMvc.perform(post("/api/accounts/transfer")
                .param("fromAccountId", account2.getId().toString())
                .param("toAccountId", account1.getId().toString())
                .param("amount", "1000"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> 
                    result.getResolvedException().getMessage().contains("Saldo insuficiente"));
    }
}
