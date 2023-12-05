package com.gdu.petmall.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.OrderDto;
import com.gdu.petmall.service.PayService;

import lombok.RequiredArgsConstructor;

@RequestMapping
@RequiredArgsConstructor
@Controller
public class PayController {
   
    private final PayService payService;
    
    @GetMapping("/pay/payFail")
    public String list() {
      return "pay/payFail";
    }
    
    //내 주문 조회로 이동
    @GetMapping("/user/mypage/orderList.do")
    public String getOrderList(HttpServletRequest request, Model model) {
    	
    	Map<String, Object> resultMap = payService.myPayList(request);
    	model.addAttribute("myPayList", resultMap.get("myPayList"));
    	
      return "user/orderList";
    }
    
    @GetMapping("/pay/complete")
    public String pay(OrderDto order, RedirectAttributes redirectAttributes) {
    	int payComplete = payService.payComplete(order);
    	redirectAttributes.addFlashAttribute("payService", payService);
    	return "pay/complete";
    }
}