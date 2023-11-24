package com.gdu.petmall.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.petmall.dto.CartDto;
import com.gdu.petmall.service.CartService;

import lombok.RequiredArgsConstructor;
import reactor.netty.http.server.HttpServerResponse;

@RequiredArgsConstructor
@Controller
public class CartController {

 private final CartService cartService;
  
  
  @GetMapping(value="/order/detail.do")
  public String orderDetail(HttpServletRequest request, Model model) {
    cartService.getList(request, model);
    return "/order/detail";
  }
  
  @GetMapping(value="/order/cart.go")
  public String cartList(HttpServletRequest request, Model model){
    cartService.getList(request, model);
    return "/order/cart";
  }
  
  @PostMapping(value="/order/addCart.do")
  public String addCart(HttpServletRequest request, Model model) {
    cartService.addCart(request, model);
    return "redirect:/order/cart.go"; 
  }   
  
  @ResponseBody
  @PostMapping(value="/order/delete.do", produces="application/json")
  public Map<String, Object> deleteCart(HttpServletRequest request) {
    return cartService.deleteCart(request);
  }
  
  @ResponseBody
  @PostMapping(value="/order/update.do", produces="application/json")
  public String updateCart(HttpServletRequest request) {
    cartService.modifyCart(request);
    return "/order/cart";
    
  }




}

  
  
  
