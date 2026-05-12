package com.personalfinance.tracker.budget.service;

import com.personalfinance.tracker.budget.dto.BudgetRequest;
import com.personalfinance.tracker.budget.vo.BudgetOverviewVO;
import com.personalfinance.tracker.budget.vo.BudgetVO;

import java.util.List;

public interface BudgetService {

    BudgetVO create(BudgetRequest request);

    BudgetVO update(Long id, BudgetRequest request);

    void delete(Long id);

    List<BudgetVO> listByMonth(String month);

    BudgetOverviewVO overview(String month);
}
