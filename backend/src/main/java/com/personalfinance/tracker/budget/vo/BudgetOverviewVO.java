package com.personalfinance.tracker.budget.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BudgetOverviewVO {

    private String month;
    private BigDecimal totalBudget;
    private BigDecimal totalSpent;
    private BigDecimal remainingAmount;
    private BigDecimal usageRate;
    private Boolean overBudget;
    private List<BudgetVO> categoryBudgets;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(BigDecimal usageRate) {
        this.usageRate = usageRate;
    }

    public Boolean getOverBudget() {
        return overBudget;
    }

    public void setOverBudget(Boolean overBudget) {
        this.overBudget = overBudget;
    }

    public List<BudgetVO> getCategoryBudgets() {
        return categoryBudgets;
    }

    public void setCategoryBudgets(List<BudgetVO> categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }
}
