package com.personalfinance.tracker.bill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personalfinance.tracker.bill.entity.Bill;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {
}

