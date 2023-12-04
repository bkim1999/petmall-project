package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.OrderDto;

@Mapper
public interface OrderMapper {
      public int insertOrderPay(OrderDto order);
      
      public List<OrderDto> getMyOrderList(Map<String, Object> map);

  
       public  List<OrderDto> getOrder(OrderDto orderDto);
}