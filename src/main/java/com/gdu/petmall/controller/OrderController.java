package com.gdu.petmall.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.OrderDto;
import com.gdu.petmall.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class OrderController {
   
  private final OrderService orderService;
  
  @PostMapping("/order/add.do")
  public String orderDetail(MultipartHttpServletRequest multipartRequest,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) throws Exception {
      boolean addResult = orderService.addOrder(multipartRequest);
      redirectAttributes.addFlashAttribute("addResult", addResult);
      
      return "redirect:/pay/payment";    
  }


   
}