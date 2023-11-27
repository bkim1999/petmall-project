package com.gdu.petmall.service;

import com.gdu.petmall.dao.PayMapper;
import com.gdu.petmall.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayServiceImpl implements PayService {

    private final PayMapper payMapper;

    
    public PayServiceImpl(PayMapper payMapper) {
        this.payMapper = payMapper;
    }

    @Override
    public List<OrderDto> getPayment() {
        return payMapper.getPayment();
    }
}