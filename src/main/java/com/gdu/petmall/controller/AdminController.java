package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.petmall.service.AdminService;
import com.gdu.petmall.service.InquiryService;
import com.gdu.petmall.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
  
  private final AdminService adminService;
  private final ProductService productService;
  private final InquiryService inquiryService;
  
  
  @GetMapping("/admin.go")
  public String adminlist() {
    return "admin/list";
  }
  
  @GetMapping("/product_list.go")
  public String productDetailList() {
    return "admin/product_list";
  }
  
  
  @GetMapping("/user_list.go")
  public String userList() {
    return "admin/user_list";
  }
  
  @ResponseBody
  @GetMapping(value="/user_list.do", produces="application/json")
  public Map<String, Object> userDetailList() {
    return adminService.getUser();
  }
  
  @ResponseBody
  @GetMapping(value="/admin.ajaxALL", produces="application/json")
  public Map<String, Object> getAjaxAlllist() {
    return adminService.getAjaxAlllist();
  }
  
  @ResponseBody
  @GetMapping(value="/adminanswer.ajaxALL", produces="application/json")
  public Map<String, Object> getAjaxAnswerList(HttpServletRequest request) {
    return adminService.getAjaxAnswerList(request);
  }
  
  @ResponseBody
  @GetMapping(value="/getlist.do", produces="application/json")
  public Map<String, Object> productDetailList(HttpServletRequest request) {
    return productService.loadProductList(request);
  }
  
  @GetMapping("/qna_list.go")
  public String qnaDetaillist(HttpServletRequest request, Model model) {
    adminService.getQna(request, model);
    return "admin/qna_list";
  }
  
  @GetMapping("/event_list.go")
  public String list() {
    return "admin/event_list";
  }
  
  @ResponseBody
  @GetMapping(value="/event_list.do", produces="application/json")
  public Map<String, Object> eventDetailList() {
    return adminService.getEvent();
  }
  
  @GetMapping(value="/partner_list.go")
  public String partnerList(HttpServletRequest request, Model model) {
    inquiryService.getInquiryList(request, model);
    return "admin/partner_list";
  }
  
  @ResponseBody
  @GetMapping(value="/pw_init.do", produces = "application/json")
  public Map<String, Object> pwInit(HttpServletRequest request) {
    return adminService.pwInit(request);
  }
  
  @ResponseBody
  @GetMapping(value="/adminTake.do", produces = "application/json")
  public Map<String, Object> adminTake(HttpServletRequest request) {
    return adminService.adminTake(request);
  }
  
  @ResponseBody
  @GetMapping(value="/normalTake.do", produces = "application/json")
  public Map<String, Object> normalTake(HttpServletRequest request) {
    return adminService.normalTake(request);
  }

}
