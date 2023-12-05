package com.gdu.petmall.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
      
      return "redirect:/order/myOrderList";    
  }
  
  @GetMapping("/order/myOrderList")
  public String myOrderList(HttpServletRequest request, Model model) {
     
      Map<String, Object> resultMap = orderService.myOrderList(request);
      model.addAttribute("myOrderList", resultMap.get("myOrderList"));
      
      return "order/myOrderList";
  }
  
  @GetMapping("/pay/payment")
  public String detail(@RequestParam(value = "totalPrice", defaultValue = "0") int totalPrice,String yourOrder, Model model) {
      List<OrderDto> order = orderService.getOrder(totalPrice, yourOrder);
      model.addAttribute("order", order);
      return "pay/payment";
  }
  


   
}