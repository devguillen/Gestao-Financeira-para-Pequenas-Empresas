package com.gestao.financas.service;

import com.gestao.financas.entity.Account;
import com.gestao.financas.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account1 = new Account();
        account1.setId(1L);
        account1.setName("Conta Corrente");
        account1.setType("Checking");
        account1.setBalance(BigDecimal.valueOf(1000));

        account2 = new Account();
        account2.setId(2L);
        account2.setName("Poupança");
        account2.setType("Savings");
        account2.setBalance(BigDecimal.valueOf(500));
    }

    @Test
    void testCreateAccount() {
        when(accountRepository.save(account1)).thenReturn(account1);

        Account created = accountService.createAccount(account1);

        assertNotNull(created);
        assertEquals("Conta Corrente", created.getName());
        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void testGetAccountsByUser() {
        when(accountRepository.findByUserId(1L)).thenReturn(Arrays.asList(account1, account2));

        List<Account> accounts = accountService.getAccountsByUser(1L);

        assertEquals(2, accounts.size());
        verify(accountRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account updatedDetails = new Account();
        updatedDetails.setName("Conta Atualizada");
        updatedDetails.setType("Checking");
        updatedDetails.setBalance(BigDecimal.valueOf(1500));

        Account updated = accountService.updateAccount(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Conta Atualizada", updated.getName());
        assertEquals(BigDecimal.valueOf(1500), updated.getBalance());
        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void testDeleteAccount() {
        doNothing().when(accountRepository).deleteById(1L);

        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void testTransfer() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account2));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.transfer(1L, 2L, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(800), account1.getBalance());
        assertEquals(BigDecimal.valueOf(700), account2.getBalance());
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void testTransferInsufficientBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account2));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.transfer(1L, 2L, BigDecimal.valueOf(2000)));

        assertEquals("Saldo insuficiente para transferência", exception.getMessage());
    }
}
