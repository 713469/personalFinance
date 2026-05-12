package com.personalfinance.tracker.category.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personalfinance.tracker.bill.entity.Bill;
import com.personalfinance.tracker.bill.mapper.BillMapper;
import com.personalfinance.tracker.category.converter.CategoryConverter;
import com.personalfinance.tracker.category.dto.CategoryRequest;
import com.personalfinance.tracker.category.dto.CategoryResponse;
import com.personalfinance.tracker.category.entity.Category;
import com.personalfinance.tracker.category.mapper.CategoryMapper;
import com.personalfinance.tracker.category.service.CategoryService;
import com.personalfinance.tracker.common.BusinessException;
import com.personalfinance.tracker.common.ErrorCode;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final BillMapper billMapper;

    public CategoryServiceImpl(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = CategoryConverter.toEntity(request);
        save(category);
        return CategoryConverter.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getRequiredCategory(id);
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setSort(request.getSort());
        category.setStatus(request.getStatus());
        updateById(category);
        return CategoryConverter.toResponse(category);
    }

    @Override
    public CategoryResponse detail(Long id) {
        return CategoryConverter.toResponse(getRequiredCategory(id));
    }

    @Override
    public List<CategoryResponse> listAll() {
        return lambdaQuery()
            .orderByAsc(Category::getSort)
            .list()
            .stream()
            .map(CategoryConverter::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> listByType(String type) {
        return lambdaQuery()
            .eq(Category::getType, type)
            .orderByAsc(Category::getSort)
            .list()
            .stream()
            .map(CategoryConverter::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Integer status) {
        Category category = getRequiredCategory(id);
        category.setStatus(status);
        updateById(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = getRequiredCategory(id);
        Long usedCount = billMapper.selectCount(new LambdaQueryWrapper<Bill>().eq(Bill::getCategoryId, category.getId()));
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_IN_USE);
        }
        removeById(id);
    }

    private Category getRequiredCategory(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }
}
