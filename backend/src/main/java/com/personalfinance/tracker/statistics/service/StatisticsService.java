package com.personalfinance.tracker.statistics.service;

import java.time.YearMonth;
import java.util.List;
import com.personalfinance.tracker.statistics.vo.AccountBalanceVO;
import com.personalfinance.tracker.statistics.vo.CategoryExpenseVO;
import com.personalfinance.tracker.statistics.vo.MonthlyTrendVO;
import com.personalfinance.tracker.statistics.vo.SummaryVO;

public interface StatisticsService {

    SummaryVO summary(YearMonth month);

    List<CategoryExpenseVO> categoryExpense(YearMonth month);

    List<MonthlyTrendVO> monthlyTrend(int monthCount);

    List<AccountBalanceVO> accountBalance();
}
