package com.gdu.petmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.OrderDto;

@Mapper
public interface OrderMapper {
      public int insertOrderPay(OrderDto order);
      
}