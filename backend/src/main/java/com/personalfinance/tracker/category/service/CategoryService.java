package com.personalfinance.tracker.category.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.personalfinance.tracker.category.dto.CategoryRequest;
import com.personalfinance.tracker.category.dto.CategoryResponse;
import com.personalfinance.tracker.category.entity.Category;

public interface CategoryService extends IService<Category> {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse detail(Long id);

    List<CategoryResponse> listAll();

    List<CategoryResponse> listByType(String type);

    void changeStatus(Long id, Integer status);

    void delete(Long id);
}
