package com.personalfinance.tracker.bill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personalfinance.tracker.bill.dto.BillPageQuery;
import com.personalfinance.tracker.bill.dto.BillRequest;
import com.personalfinance.tracker.bill.dto.BillResponse;
import com.personalfinance.tracker.bill.entity.Bill;
import com.personalfinance.tracker.common.PageResult;

public interface BillService extends IService<Bill> {

    BillResponse create(BillRequest request);

    BillResponse update(Long id, BillRequest request);

    BillResponse detail(Long id);

    PageResult<BillResponse> page(BillPageQuery query);

    void delete(Long id);
}
