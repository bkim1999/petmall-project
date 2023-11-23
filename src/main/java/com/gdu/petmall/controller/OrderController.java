package com.gdu.petmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
  
  @GetMapping("/order/order.form")
  public String orderForm() {
    return "order/order";
  }
  
  @PostMapping("/order/order.do")
  public String order() {
    return "pay/pay.form";
  }
  
}
