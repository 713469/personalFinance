package com.personalfinance.tracker.account.controller;

import java.util.List;
import com.personalfinance.tracker.account.dto.AccountRequest;
import com.personalfinance.tracker.account.dto.AccountResponse;
import com.personalfinance.tracker.account.service.AccountService;
import com.personalfinance.tracker.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ApiResponse<List<AccountResponse>> list() {
        return ApiResponse.success(accountService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<AccountResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(accountService.detail(id));
    }

    @PostMapping
    public ApiResponse<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        return ApiResponse.success(accountService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AccountResponse> update(@PathVariable Long id, @Valid @RequestBody AccountRequest request) {
        return ApiResponse.success(accountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ApiResponse.success(null);
    }
}
