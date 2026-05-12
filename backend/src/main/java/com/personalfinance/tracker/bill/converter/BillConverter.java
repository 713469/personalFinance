package com.personalfinance.tracker.bill.converter;

import com.personalfinance.tracker.account.entity.Account;
import com.personalfinance.tracker.bill.dto.BillRequest;
import com.personalfinance.tracker.bill.dto.BillResponse;
import com.personalfinance.tracker.bill.entity.Bill;
import com.personalfinance.tracker.category.entity.Category;

public final class BillConverter {

    private BillConverter() {
    }

    public static Bill toEntity(BillRequest request) {
        Bill bill = new Bill();
        bill.setType(request.getType());
        bill.setAmount(request.getAmount());
        bill.setCategoryId(request.getCategoryId());
        bill.setAccountId(request.getAccountId());
        bill.setTradeTime(request.getTradeTime());
        bill.setRemark(request.getRemark());
        bill.setDeleted(0);
        return bill;
    }

    public static BillResponse toResponse(Bill bill, Account account, Category category) {
        BillResponse response = new BillResponse();
        response.setId(bill.getId());
        response.setType(bill.getType());
        response.setAmount(bill.getAmount());
        response.setCategoryId(bill.getCategoryId());
        response.setCategoryName(category == null ? null : category.getName());
        response.setAccountId(bill.getAccountId());
        response.setAccountName(account == null ? null : account.getName());
        response.setTradeTime(bill.getTradeTime());
        response.setRemark(bill.getRemark());
        response.setCreateTime(bill.getCreateTime());
        response.setUpdateTime(bill.getUpdateTime());
        return response;
    }
}

