package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.petmall.dto.CartOptionListDto;
import com.gdu.petmall.service.CartService;

import lombok.RequiredArgsConstructor;


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
  public String addCart(@ModelAttribute CartOptionListDto cartList,Model model) {
    System.out.println("ddd:" + cartList);
    cartService.addCart(cartList, model);
    return "redirect:/order/cart.go"; 
  }
  
  
  
  @ResponseBody
  @PostMapping(value="/order/delete.do", produces="application/json")
  public Map<String, Object> deleteCart(HttpServletRequest request) {
   return cartService.deleteCart(request);
  }
  
  @ResponseBody
  @PostMapping(value="/order/minusupdate.do", produces="application/json")
  public Map<String, Object> minusUpdateCart(HttpServletRequest request) {
    System.out.println("minusrequest:" + request.getParameter("count"));
    return cartService.minusCart(request);
  }

  @ResponseBody
  @PostMapping(value="/order/plusupdate.do", produces="application/json")
  public Map<String, Object> plusUupdateCart(HttpServletRequest request) {
    System.out.println("plusrequest:" + request.getParameter("count"));
    return cartService.plusCart(request);
  }
  

}