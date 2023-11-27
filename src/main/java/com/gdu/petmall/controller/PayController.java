package com.gdu.petmall.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdu.petmall.dto.OrderDto;
import com.gdu.petmall.service.PayService;

import lombok.RequiredArgsConstructor;

@RequestMapping
@RequiredArgsConstructor
@Controller
public class PayController {
    private final PayService payService;

    @GetMapping("/pay/payment.do") 
    public String list(Model model) {
        List<OrderDto> orderPay = payService.getPayment(); 
        model.addAttribute("orderPay", orderPay);
        return "pay/payment"; 
    }
}
