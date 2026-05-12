package com.personalfinance.tracker.account.service;

import java.math.BigDecimal;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.personalfinance.tracker.account.dto.AccountRequest;
import com.personalfinance.tracker.account.dto.AccountResponse;
import com.personalfinance.tracker.account.entity.Account;

public interface AccountService extends IService<Account> {

    AccountResponse create(AccountRequest request);

    AccountResponse update(Long id, AccountRequest request);

    AccountResponse detail(Long id);

    List<AccountResponse> listAll();

    void adjustBalance(Long accountId, BigDecimal delta);

    void delete(Long id);
}
