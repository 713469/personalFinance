package com.personalfinance.tracker.statistics.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.personalfinance.tracker.account.service.AccountService;
import com.personalfinance.tracker.bill.entity.Bill;
import com.personalfinance.tracker.bill.service.BillService;
import com.personalfinance.tracker.category.entity.Category;
import com.personalfinance.tracker.category.service.CategoryService;
import com.personalfinance.tracker.statistics.service.StatisticsService;
import com.personalfinance.tracker.statistics.vo.AccountBalanceVO;
import com.personalfinance.tracker.statistics.vo.CategoryExpenseVO;
import com.personalfinance.tracker.statistics.vo.MonthlyTrendVO;
import com.personalfinance.tracker.statistics.vo.SummaryVO;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final BillService billService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    public StatisticsServiceImpl(BillService billService, AccountService accountService, CategoryService categoryService) {
        this.billService = billService;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    @Override
    public SummaryVO summary(YearMonth month) {
        List<Bill> bills = billsInMonth(month);
        BigDecimal income = totalByType(bills, "INCOME");
        BigDecimal expense = totalByType(bills, "EXPENSE");

        SummaryVO vo = new SummaryVO();
        vo.setMonth(month.toString());
        vo.setIncome(income);
        vo.setExpense(expense);
        vo.setBalance(income.subtract(expense));
        vo.setTotalAssets(accountService.list().stream()
            .map(account -> account.getCurrentBalance() == null ? BigDecimal.ZERO : account.getCurrentBalance())
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }

    @Override
    public List<CategoryExpenseVO> categoryExpense(YearMonth month) {
        List<Bill> bills = billsInMonth(month).stream()
            .filter(bill -> "EXPENSE".equalsIgnoreCase(bill.getType()))
            .collect(Collectors.toList());
        Map<Long, BigDecimal> totals = bills.stream()
            .collect(Collectors.groupingBy(Bill::getCategoryId,
                Collectors.mapping(Bill::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        BigDecimal totalExpense = totals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<Long, Category> categories = categoryService.list().stream()
            .collect(Collectors.toMap(Category::getId, Function.identity()));

        return totals.entrySet().stream()
            .sorted(Map.Entry.<Long, BigDecimal>comparingByValue(Comparator.reverseOrder()))
            .map(entry -> {
                CategoryExpenseVO vo = new CategoryExpenseVO();
                vo.setCategoryId(entry.getKey());
                Category category = categories.get(entry.getKey());
                vo.setCategoryName(category == null ? null : category.getName());
                vo.setAmount(entry.getValue());
                vo.setPercent(totalExpense.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : entry.getValue().multiply(BigDecimal.valueOf(100)).divide(totalExpense, 2, RoundingMode.HALF_UP));
                return vo;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyTrendVO> monthlyTrend(int monthCount) {
        List<MonthlyTrendVO> result = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = monthCount - 1; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            List<Bill> bills = billsInMonth(month);
            MonthlyTrendVO vo = new MonthlyTrendVO();
            vo.setMonth(month.toString());
            vo.setIncome(totalByType(bills, "INCOME"));
            vo.setExpense(totalByType(bills, "EXPENSE"));
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<AccountBalanceVO> accountBalance() {
        return accountService.list().stream()
            .map(account -> {
                AccountBalanceVO vo = new AccountBalanceVO();
                vo.setAccountId(account.getId());
                vo.setAccountName(account.getName());
                vo.setBalance(account.getCurrentBalance() == null ? BigDecimal.ZERO : account.getCurrentBalance());
                return vo;
            })
            .collect(Collectors.toList());
    }

    private List<Bill> billsInMonth(YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay().minusNanos(1);
        return billService.lambdaQuery()
            .ge(Bill::getTradeTime, start)
            .le(Bill::getTradeTime, end)
            .list();
    }

    private BigDecimal totalByType(List<Bill> bills, String type) {
        return bills.stream()
            .filter(bill -> type.equalsIgnoreCase(bill.getType()))
            .map(Bill::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
