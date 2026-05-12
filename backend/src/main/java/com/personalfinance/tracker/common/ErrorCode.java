package com.personalfinance.tracker.common;

public enum ErrorCode {

    BAD_REQUEST("400", "请求参数错误"),
    ACCOUNT_NOT_FOUND("40001", "账户不存在"),
    CATEGORY_NOT_FOUND("40002", "分类不存在"),
    BILL_NOT_FOUND("40003", "账单不存在"),
    INVALID_AMOUNT("40004", "金额必须大于 0"),
    INVALID_BILL_TYPE("40005", "账单类型只允许 INCOME 或 EXPENSE"),
    CATEGORY_TYPE_MISMATCH("40006", "分类类型与账单类型不匹配"),
    CATEGORY_IN_USE("40007", "该分类已被账单使用，不能删除，可选择禁用"),
    ACCOUNT_IN_USE("40008", "该账户已存在账单记录，不能删除"),
    BUDGET_NOT_FOUND("40009", "预算不存在"),
    BUDGET_DUPLICATE_TOTAL("40010", "该月份已存在总预算"),
    BUDGET_DUPLICATE_CATEGORY("40011", "该月份该分类已存在预算"),
    BUDGET_INVALID_MONTH("40012", "预算月份格式应为 yyyy-MM"),
    BUDGET_INVALID_TYPE("40013", "预算类型只允许 TOTAL 或 CATEGORY"),
    BUDGET_CATEGORY_REQUIRED("40014", "分类预算必须指定分类"),
    BUDGET_CATEGORY_EXPENSE_ONLY("40015", "分类预算只能绑定支出分类"),
    SYSTEM_ERROR("500", "系统繁忙，请稍后重试");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
