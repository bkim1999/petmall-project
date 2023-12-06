package com.gdu.petmall.service;

import com.gdu.petmall.dto.OrderDto;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface PayService {
	
    public int getLoggedInUserNo(HttpServletRequest request);
	
	public Map<String, Object> myPayList(HttpServletRequest request);
	
	public int payComplete(OrderDto order);
}