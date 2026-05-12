package com.personalfinance.tracker.account.converter;

import com.personalfinance.tracker.account.dto.AccountRequest;
import com.personalfinance.tracker.account.dto.AccountResponse;
import com.personalfinance.tracker.account.entity.Account;

public final class AccountConverter {

    private AccountConverter() {
    }

    public static Account toEntity(AccountRequest request) {
        Account account = new Account();
        account.setName(request.getName());
        account.setType(request.getType());
        account.setInitialBalance(request.getInitialBalance());
        account.setCurrentBalance(request.getInitialBalance());
        account.setRemark(request.getRemark());
        account.setDeleted(0);
        return account;
    }

    public static AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setName(account.getName());
        response.setType(account.getType());
        response.setInitialBalance(account.getInitialBalance());
        response.setCurrentBalance(account.getCurrentBalance());
        response.setRemark(account.getRemark());
        response.setCreateTime(account.getCreateTime());
        response.setUpdateTime(account.getUpdateTime());
        return response;
    }
}

