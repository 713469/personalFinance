package com.personalfinance.tracker.statistics.controller;

import java.time.YearMonth;
import java.util.List;
import com.personalfinance.tracker.common.ApiResponse;
import com.personalfinance.tracker.statistics.service.StatisticsService;
import com.personalfinance.tracker.statistics.vo.AccountBalanceVO;
import com.personalfinance.tracker.statistics.vo.CategoryExpenseVO;
import com.personalfinance.tracker.statistics.vo.MonthlyTrendVO;
import com.personalfinance.tracker.statistics.vo.SummaryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/summary")
    public ApiResponse<SummaryVO> summary(@RequestParam String month) {
        return ApiResponse.success(statisticsService.summary(YearMonth.parse(month)));
    }

    @GetMapping("/category-expense")
    public ApiResponse<List<CategoryExpenseVO>> categoryExpense(@RequestParam String month) {
        return ApiResponse.success(statisticsService.categoryExpense(YearMonth.parse(month)));
    }

    @GetMapping("/monthly-trend")
    public ApiResponse<List<MonthlyTrendVO>> monthlyTrend(@RequestParam(defaultValue = "6") int monthCount) {
        return ApiResponse.success(statisticsService.monthlyTrend(monthCount));
    }

    @GetMapping("/account-balance")
    public ApiResponse<List<AccountBalanceVO>> accountBalance() {
        return ApiResponse.success(statisticsService.accountBalance());
    }
}
