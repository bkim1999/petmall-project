package com.gdu.petmall.service;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface OrderService {

    public boolean addOrder(MultipartHttpServletRequest multipartRequest) throws Exception;
    
}