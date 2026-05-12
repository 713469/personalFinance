package com.personalfinance.tracker.category.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public class CategoryRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "分类类型只允许 INCOME 或 EXPENSE")
    private String type;

    private String icon;

    @NotNull
    @PositiveOrZero
    private Integer sort;

    @NotNull
    @Min(0)
    @Max(1)
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

