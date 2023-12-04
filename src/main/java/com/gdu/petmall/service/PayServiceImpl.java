package com.gdu.petmall.service;

import com.gdu.petmall.dao.PayMapper;
import com.gdu.petmall.dto.OrderDto;
import com.gdu.petmall.dto.UserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class PayServiceImpl implements PayService {
	
	private final PayMapper payMapper;
    @Autowired
    public PayServiceImpl(PayMapper payMapper) {
        this.payMapper = payMapper;
    }
    @Override
    public int getLoggedInUserNo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            UserDto loggedInUser = (UserDto) session.getAttribute("user");
            return loggedInUser.getUserNo();
        } else {
            return -1;
        }
    }
    @Override
    public Map<String, Object> myPayList(HttpServletRequest request) {
        int userNo = getLoggedInUserNo(request); 

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userNo", userNo);

        List<OrderDto> myPayList = payMapper.getMyPayList(paramMap);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("myPayList", myPayList);

        return resultMap;
    }
    @Override
    public int payComplete(OrderDto order) {
    	return payMapper.updatePay(order);
    }
}