package com.gdu.petmall.service;

import com.gdu.petmall.dto.OrderDto;
import java.util.List;

public interface PayService {
    List<OrderDto> getPayment();
}