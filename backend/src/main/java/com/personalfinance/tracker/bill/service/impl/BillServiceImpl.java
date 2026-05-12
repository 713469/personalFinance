package com.personalfinance.tracker.bill.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personalfinance.tracker.account.entity.Account;
import com.personalfinance.tracker.account.service.AccountService;
import com.personalfinance.tracker.bill.converter.BillConverter;
import com.personalfinance.tracker.bill.dto.BillPageQuery;
import com.personalfinance.tracker.bill.dto.BillRequest;
import com.personalfinance.tracker.bill.dto.BillResponse;
import com.personalfinance.tracker.bill.entity.Bill;
import com.personalfinance.tracker.bill.mapper.BillMapper;
import com.personalfinance.tracker.bill.service.BillService;
import com.personalfinance.tracker.category.entity.Category;
import com.personalfinance.tracker.category.service.CategoryService;
import com.personalfinance.tracker.common.BusinessException;
import com.personalfinance.tracker.common.ErrorCode;
import com.personalfinance.tracker.common.PageResult;

@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    private final AccountService accountService;
    private final CategoryService categoryService;

    public BillServiceImpl(AccountService accountService, CategoryService categoryService) {
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    @Override
    @Transactional
    public BillResponse create(BillRequest request) {
        validateRequest(request);
        validateReferences(request);
        Bill bill = BillConverter.toEntity(request);
        save(bill);
        applyBalanceEffect(bill, false);
        return toResponse(bill);
    }

    @Override
    @Transactional
    public BillResponse update(Long id, BillRequest request) {
        Bill oldBill = getRequiredBill(id);
        validateRequest(request);
        validateReferences(request);
        rollbackBalanceEffect(oldBill);
        oldBill.setType(request.getType());
        oldBill.setAmount(request.getAmount());
        oldBill.setCategoryId(request.getCategoryId());
        oldBill.setAccountId(request.getAccountId());
        oldBill.setTradeTime(request.getTradeTime());
        oldBill.setRemark(request.getRemark());
        updateById(oldBill);
        applyBalanceEffect(oldBill, false);
        return toResponse(oldBill);
    }

    @Override
    public BillResponse detail(Long id) {
        return toResponse(getRequiredBill(id));
    }

    @Override
    public PageResult<BillResponse> page(BillPageQuery query) {
        Page<Bill> page = new Page<>(query.getPageNum(), query.getPageSize());
        LocalDateTime startTime = query.getStartTime();
        LocalDateTime endTime = query.getEndTime();
        if (query.getMonth() != null && !query.getMonth().isBlank()) {
            YearMonth month = YearMonth.parse(query.getMonth());
            startTime = month.atDay(1).atStartOfDay();
            endTime = month.plusMonths(1).atDay(1).atStartOfDay().minusNanos(1);
        }
        IPage<Bill> result = lambdaQuery()
            .eq(query.getType() != null && !query.getType().isBlank(), Bill::getType, query.getType())
            .eq(query.getCategoryId() != null, Bill::getCategoryId, query.getCategoryId())
            .eq(query.getAccountId() != null, Bill::getAccountId, query.getAccountId())
            .ge(startTime != null, Bill::getTradeTime, startTime)
            .le(endTime != null, Bill::getTradeTime, endTime)
            .orderByDesc(Bill::getTradeTime)
            .page(page);

        List<BillResponse> records = result.getRecords().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Bill bill = getRequiredBill(id);
        rollbackBalanceEffect(bill);
        removeById(id);
    }

    private Bill getRequiredBill(Long id) {
        Bill bill = getById(id);
        if (bill == null) {
            throw new BusinessException(ErrorCode.BILL_NOT_FOUND);
        }
        return bill;
    }

    private void validateRequest(BillRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
        if (!"INCOME".equalsIgnoreCase(request.getType()) && !"EXPENSE".equalsIgnoreCase(request.getType())) {
            throw new BusinessException(ErrorCode.INVALID_BILL_TYPE);
        }
    }

    private void validateReferences(BillRequest request) {
        Account account = accountService.getById(request.getAccountId());
        if (account == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Category category = categoryService.getById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        if (!request.getType().equalsIgnoreCase(category.getType())) {
            throw new BusinessException(ErrorCode.CATEGORY_TYPE_MISMATCH);
        }
    }

    private void applyBalanceEffect(Bill bill, boolean reverse) {
        BigDecimal delta = bill.getAmount();
        if ("EXPENSE".equalsIgnoreCase(bill.getType())) {
            delta = delta.negate();
        }
        if (reverse) {
            delta = delta.negate();
        }
        accountService.adjustBalance(bill.getAccountId(), delta);
    }

    private void rollbackBalanceEffect(Bill bill) {
        applyBalanceEffect(bill, true);
    }

    private BillResponse toResponse(Bill bill) {
        Account account = accountService.getById(bill.getAccountId());
        Category category = categoryService.getById(bill.getCategoryId());
        return BillConverter.toResponse(bill, account, category);
    }
}
