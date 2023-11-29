package com.gdu.petmall.service;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dto.OrderDto;

public interface OrderService {

    public boolean addOrder(MultipartHttpServletRequest multipartRequest) throws Exception;

}