package com.personalfinance.tracker.budget.controller;

import com.personalfinance.tracker.budget.dto.BudgetRequest;
import com.personalfinance.tracker.budget.service.BudgetService;
import com.personalfinance.tracker.budget.vo.BudgetOverviewVO;
import com.personalfinance.tracker.budget.vo.BudgetVO;
import com.personalfinance.tracker.common.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ApiResponse<BudgetVO> create(@Valid @RequestBody BudgetRequest request) {
        return ApiResponse.success(budgetService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BudgetVO> update(@PathVariable Long id, @Valid @RequestBody BudgetRequest request) {
        return ApiResponse.success(budgetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<BudgetVO>> listByMonth(@RequestParam String month) {
        return ApiResponse.success(budgetService.listByMonth(month));
    }

    @GetMapping("/overview")
    public ApiResponse<BudgetOverviewVO> overview(@RequestParam String month) {
        return ApiResponse.success(budgetService.overview(month));
    }
}
