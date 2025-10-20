package com.gestao.financas;

import com.gestao.financas.entity.Account;
import com.gestao.financas.repository.AccountRepository;
import com.gestao.financas.service.AccountService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        account1.setName("Conta 1");
        account1.setType("Bank");
        account1.setBalance(new BigDecimal("1000"));

        account2 = new Account();
        account2.setId(2L);
        account2.setName("Conta 2");
        account2.setType("Cash");
        account2.setBalance(new BigDecimal("500"));
    }

    @Test
    void testCreateAccount() {
        when(accountRepository.save(account1)).thenReturn(account1);

        Account created = accountService.createAccount(account1);

        assertNotNull(created);
        assertEquals("Conta 1", created.getName());
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
        Account updatedDetails = new Account();
        updatedDetails.setName("Conta Atualizada");
        updatedDetails.setType("Bank Updated");
        updatedDetails.setBalance(new BigDecimal("2000"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedDetails);

        Account updated = accountService.updateAccount(1L, updatedDetails);

        assertEquals("Conta Atualizada", updated.getName());
        assertEquals(new BigDecimal("2000"), updated.getBalance());
        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void testUpdateAccount_NotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.updateAccount(99L, account1));

        assertEquals("Conta não encontrada", exception.getMessage());
    }

    @Test
    void testDeleteAccount() {
        doNothing().when(accountRepository).deleteById(1L);

        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void testTransfer_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account2));

        accountService.transfer(1L, 2L, new BigDecimal("300"));

        assertEquals(new BigDecimal("700"), account1.getBalance());
        assertEquals(new BigDecimal("800"), account2.getBalance());
        verify(accountRepository, times(1)).save(account1);
        verify(accountRepository, times(1)).save(account2);
    }

    @Test
    void testTransfer_InsufficientBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account2));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.transfer(1L, 2L, new BigDecimal("2000")));

        assertEquals("Saldo insuficiente para transferência", exception.getMessage());
    }

    @Test
    void testTransfer_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.transfer(1L, 2L, new BigDecimal("100")));

        assertEquals("Conta de origem não encontrada", exception.getMessage());
    }
}
