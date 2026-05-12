package com.personalfinance.tracker.category.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personalfinance.tracker.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

