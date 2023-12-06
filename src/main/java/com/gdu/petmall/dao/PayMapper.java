package com.gdu.petmall.dao;

import com.gdu.petmall.dto.OrderDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PayMapper {

    public List<OrderDto> getMyPayList(Map<String, Object> map);
    
    public int updatePay(OrderDto order);
}