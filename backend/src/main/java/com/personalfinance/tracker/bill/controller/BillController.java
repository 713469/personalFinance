package com.personalfinance.tracker.bill.controller;

import com.personalfinance.tracker.bill.dto.BillPageQuery;
import com.personalfinance.tracker.bill.dto.BillRequest;
import com.personalfinance.tracker.bill.dto.BillResponse;
import com.personalfinance.tracker.bill.service.BillService;
import com.personalfinance.tracker.common.ApiResponse;
import com.personalfinance.tracker.common.PageResult;
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
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public ApiResponse<PageResult<BillResponse>> page(BillPageQuery query) {
        return ApiResponse.success(billService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<BillResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(billService.detail(id));
    }

    @PostMapping
    public ApiResponse<BillResponse> create(@Valid @RequestBody BillRequest request) {
        return ApiResponse.success(billService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BillResponse> update(@PathVariable Long id, @Valid @RequestBody BillRequest request) {
        return ApiResponse.success(billService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        billService.delete(id);
        return ApiResponse.success(null);
    }
}
