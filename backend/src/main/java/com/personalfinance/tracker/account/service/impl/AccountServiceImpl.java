package com.personalfinance.tracker.account.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personalfinance.tracker.account.converter.AccountConverter;
import com.personalfinance.tracker.account.dto.AccountRequest;
import com.personalfinance.tracker.account.dto.AccountResponse;
import com.personalfinance.tracker.account.entity.Account;
import com.personalfinance.tracker.account.mapper.AccountMapper;
import com.personalfinance.tracker.account.service.AccountService;
import com.personalfinance.tracker.bill.entity.Bill;
import com.personalfinance.tracker.bill.mapper.BillMapper;
import com.personalfinance.tracker.common.BusinessException;
import com.personalfinance.tracker.common.ErrorCode;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private final BillMapper billMapper;

    public AccountServiceImpl(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    @Override
    @Transactional
    public AccountResponse create(AccountRequest request) {
        Account account = AccountConverter.toEntity(request);
        save(account);
        return AccountConverter.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse update(Long id, AccountRequest request) {
        Account account = getRequiredAccount(id);
        account.setName(request.getName());
        account.setType(request.getType());
        account.setRemark(request.getRemark());
        updateById(account);
        return AccountConverter.toResponse(account);
    }

    @Override
    public AccountResponse detail(Long id) {
        return AccountConverter.toResponse(getRequiredAccount(id));
    }

    @Override
    public List<AccountResponse> listAll() {
        return lambdaQuery().list().stream().map(AccountConverter::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void adjustBalance(Long accountId, BigDecimal delta) {
        Account account = getRequiredAccount(accountId);
        account.setCurrentBalance(account.getCurrentBalance().add(delta));
        updateById(account);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Account account = getRequiredAccount(id);
        Long usedCount = billMapper.selectCount(new LambdaQueryWrapper<Bill>().eq(Bill::getAccountId, account.getId()));
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_IN_USE);
        }
        removeById(id);
    }

    private Account getRequiredAccount(Long id) {
        Account account = getById(id);
        if (account == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return account;
    }
}
