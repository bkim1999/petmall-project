package com.gdu.petmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdu.petmall.service.PayService;

import lombok.RequiredArgsConstructor;

@RequestMapping
@RequiredArgsConstructor
@Controller
public class PayController {
    private final PayService payService;

    @GetMapping("/pay/payment.do") 
    public String list() {
        return "pay/payment"; 
    }
 
    
    
}
