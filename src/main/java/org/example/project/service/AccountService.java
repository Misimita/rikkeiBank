package org.example.project.service;

import org.example.project.dto.AccountResponseDto;
import org.example.project.entity.Account;
import org.example.project.entity.User;
import org.example.project.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccountForUser(User user) {
        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber("ACC" + System.currentTimeMillis());
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    // FR-06: Vấn tin số dư
    public AccountResponseDto getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        AccountResponseDto dto = new AccountResponseDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        return dto;
    }

    // FR-05: Quản lý tài khoản với phân trang
    public List<AccountResponseDto> getAllAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accountsPage = accountRepository.findAll(pageable);
        return accountsPage.getContent().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private AccountResponseDto convertToDto(Account account) {
        AccountResponseDto dto = new AccountResponseDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        return dto;
    }
}