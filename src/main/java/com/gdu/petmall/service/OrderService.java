package com.gdu.petmall.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dto.OrderDto;

public interface OrderService {

    public boolean addOrder(MultipartHttpServletRequest multipartRequest) throws Exception;
    
    public int getLoggedInUserNo(HttpServletRequest request);
    
    public Map<String, Object> myOrderList(HttpServletRequest request);
    
    public  List<OrderDto> getOrder(int totalPrice, String yourOrder);
}