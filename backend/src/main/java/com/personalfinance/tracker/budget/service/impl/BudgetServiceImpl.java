package com.personalfinance.tracker.budget.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personalfinance.tracker.budget.dto.BudgetRequest;
import com.personalfinance.tracker.budget.entity.Budget;
import com.personalfinance.tracker.budget.mapper.BudgetMapper;
import com.personalfinance.tracker.budget.service.BudgetService;
import com.personalfinance.tracker.budget.vo.BudgetOverviewVO;
import com.personalfinance.tracker.budget.vo.BudgetVO;
import com.personalfinance.tracker.bill.entity.Bill;
import com.personalfinance.tracker.bill.service.BillService;
import com.personalfinance.tracker.category.entity.Category;
import com.personalfinance.tracker.category.service.CategoryService;
import com.personalfinance.tracker.common.BusinessException;
import com.personalfinance.tracker.common.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    private static final Logger log = LoggerFactory.getLogger(BudgetServiceImpl.class);
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    private final BillService billService;
    private final CategoryService categoryService;

    public BudgetServiceImpl(BillService billService, CategoryService categoryService) {
        this.billService = billService;
        this.categoryService = categoryService;
    }

    @Override
    @Transactional
    public BudgetVO create(BudgetRequest request) {
        validateMonth(request.getMonth());
        validateType(request.getType());
        if (!"TOTAL".equals(request.getType()) && !"CATEGORY".equals(request.getType())) {
            throw new BusinessException(ErrorCode.BUDGET_INVALID_TYPE);
        }
        if ("CATEGORY".equals(request.getType())) {
            if (request.getCategoryId() == null) {
                throw new BusinessException(ErrorCode.BUDGET_CATEGORY_REQUIRED);
            }
            validateCategory(request.getCategoryId());
        }

        checkDuplicate(request.getMonth(), request.getType(), request.getCategoryId());

        Budget budget = new Budget();
        budget.setMonth(request.getMonth());
        budget.setType(request.getType());
        budget.setCategoryId(request.getCategoryId());
        budget.setAmount(request.getAmount());
        budget.setRemark(request.getRemark());

        save(budget);
        return toVO(budget);
    }

    @Override
    @Transactional
    public BudgetVO update(Long id, BudgetRequest request) {
        Budget budget = getById(id);
        if (budget == null) {
            throw new BusinessException(ErrorCode.BUDGET_NOT_FOUND);
        }

        budget.setAmount(request.getAmount());
        budget.setRemark(request.getRemark());
        updateById(budget);

        return toVO(budget);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Budget budget = getById(id);
        if (budget == null) {
            throw new BusinessException(ErrorCode.BUDGET_NOT_FOUND);
        }
        removeById(id);
    }

    @Override
    public List<BudgetVO> listByMonth(String month) {
        validateMonth(month);
        List<Budget> budgets = lambdaQuery()
                .eq(Budget::getMonth, month)
                .list();
        List<Bill> monthBills = billsInMonth(month);
        Map<Long, BigDecimal> categoryTotals = monthBills.stream()
                .filter(bill -> "EXPENSE".equalsIgnoreCase(bill.getType()))
                .collect(Collectors.groupingBy(Bill::getCategoryId,
                        Collectors.mapping(Bill::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        return budgets.stream()
                .map(budget -> toVOWithUsed(budget, categoryTotals))
                .collect(Collectors.toList());
    }

    @Override
    public BudgetOverviewVO overview(String month) {
        validateMonth(month);
        List<Bill> monthBills = billsInMonth(month);
        BigDecimal totalExpense = monthBills.stream()
                .filter(bill -> "EXPENSE".equalsIgnoreCase(bill.getType()))
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, BigDecimal> categoryTotals = monthBills.stream()
                .filter(bill -> "EXPENSE".equalsIgnoreCase(bill.getType()))
                .collect(Collectors.groupingBy(Bill::getCategoryId,
                        Collectors.mapping(Bill::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        List<Budget> budgets = lambdaQuery()
                .eq(Budget::getMonth, month)
                .list();

        Budget totalBudget = budgets.stream()
                .filter(b -> "TOTAL".equals(b.getType()))
                .findFirst()
                .orElse(null);

        List<Budget> categoryBudgets = budgets.stream()
                .filter(b -> "CATEGORY".equals(b.getType()))
                .collect(Collectors.toList());

        BudgetOverviewVO vo = new BudgetOverviewVO();
        vo.setMonth(month);

        if (totalBudget != null) {
            vo.setTotalBudget(totalBudget.getAmount());
            vo.setTotalSpent(totalExpense);
            vo.setRemainingAmount(totalBudget.getAmount().subtract(totalExpense));
            vo.setUsageRate(computeUsageRate(totalExpense, totalBudget.getAmount()));
            vo.setOverBudget(totalExpense.compareTo(totalBudget.getAmount()) > 0);
        } else {
            vo.setTotalBudget(BigDecimal.ZERO);
            vo.setTotalSpent(totalExpense);
            vo.setRemainingAmount(totalExpense.negate());
            vo.setUsageRate(BigDecimal.ZERO);
            vo.setOverBudget(false);
        }

        List<BudgetVO> categoryVOList = categoryBudgets.stream()
                .map(budget -> toVOWithUsed(budget, categoryTotals))
                .collect(Collectors.toList());
        vo.setCategoryBudgets(categoryVOList);

        return vo;
    }

    private void checkDuplicate(String month, String type, Long categoryId) {
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<Budget>()
                .eq(Budget::getMonth, month)
                .eq(Budget::getType, type);
        if ("CATEGORY".equals(type) && categoryId != null) {
            wrapper.eq(Budget::getCategoryId, categoryId);
        }
        long count = count(wrapper);
        if (count > 0) {
            if ("TOTAL".equals(type)) {
                throw new BusinessException(ErrorCode.BUDGET_DUPLICATE_TOTAL);
            } else {
                throw new BusinessException(ErrorCode.BUDGET_DUPLICATE_CATEGORY);
            }
        }
    }

    private void validateMonth(String month) {
        if (month == null || !MONTH_PATTERN.matcher(month).matches()) {
            throw new BusinessException(ErrorCode.BUDGET_INVALID_MONTH);
        }
    }

    private void validateType(String type) {
        if (type == null || (!"TOTAL".equals(type) && !"CATEGORY".equals(type))) {
            throw new BusinessException(ErrorCode.BUDGET_INVALID_TYPE);
        }
    }

    private void validateCategory(Long categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        if (!"EXPENSE".equalsIgnoreCase(category.getType())) {
            throw new BusinessException(ErrorCode.BUDGET_CATEGORY_EXPENSE_ONLY);
        }
    }

    private List<Bill> billsInMonth(String monthStr) {
        YearMonth ym = YearMonth.parse(monthStr);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay().minusNanos(1);
        return billService.lambdaQuery()
                .ge(Bill::getTradeTime, start)
                .le(Bill::getTradeTime, end)
                .list();
    }

    private BigDecimal computeUsageRate(BigDecimal used, BigDecimal budget) {
        if (budget.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return used.multiply(BigDecimal.valueOf(100))
                .divide(budget, 2, RoundingMode.HALF_UP);
    }

    private BudgetVO toVO(Budget budget) {
        BudgetVO vo = new BudgetVO();
        vo.setId(budget.getId());
        vo.setMonth(budget.getMonth());
        vo.setType(budget.getType());
        vo.setCategoryId(budget.getCategoryId());
        vo.setAmount(budget.getAmount());
        vo.setRemark(budget.getRemark());
        vo.setCreateTime(budget.getCreateTime());
        vo.setUpdateTime(budget.getUpdateTime());
        if (budget.getCategoryId() != null) {
            Category category = categoryService.getById(budget.getCategoryId());
            vo.setCategoryName(category != null ? category.getName() : null);
        }
        return vo;
    }

    private BudgetVO toVOWithUsed(Budget budget, Map<Long, BigDecimal> categoryTotals) {
        BudgetVO vo = toVO(budget);
        BigDecimal used;
        if ("TOTAL".equals(budget.getType())) {
            used = categoryTotals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (budget.getCategoryId() != null) {
            used = categoryTotals.getOrDefault(budget.getCategoryId(), BigDecimal.ZERO);
        } else {
            used = BigDecimal.ZERO;
        }
        vo.setUsedAmount(used);
        vo.setRemainingAmount(budget.getAmount().subtract(used));
        vo.setUsageRate(computeUsageRate(used, budget.getAmount()));
        vo.setOverBudget(used.compareTo(budget.getAmount()) > 0);
        return vo;
    }
}
