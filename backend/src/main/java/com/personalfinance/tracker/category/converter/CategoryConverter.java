package com.personalfinance.tracker.category.converter;

import com.personalfinance.tracker.category.dto.CategoryRequest;
import com.personalfinance.tracker.category.dto.CategoryResponse;
import com.personalfinance.tracker.category.entity.Category;

public final class CategoryConverter {

    private CategoryConverter() {
    }

    public static Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setSort(request.getSort());
        category.setStatus(request.getStatus());
        category.setDeleted(0);
        return category;
    }

    public static CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setType(category.getType());
        response.setIcon(category.getIcon());
        response.setSort(category.getSort());
        response.setStatus(category.getStatus());
        response.setCreateTime(category.getCreateTime());
        response.setUpdateTime(category.getUpdateTime());
        return response;
    }
}

