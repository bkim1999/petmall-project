package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.service.FaqService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class FaqController {

  private final FaqService faqService;

  @GetMapping(value="/faq/list.do")
  public String faqList(HttpServletRequest request, Model model) {
    faqService.customerFaqList(request, model);
    model.addAttribute("getFaqCategoryList", faqService.getloadFaqCategoryList());
    System.out.println("사용자:" + faqService.getloadFaqCategoryList());
    return "/faq/list";
  }
  
  @GetMapping(value="/faq/write.do")
  public String faqWrite(HttpServletRequest request, Model model) {
    faqService.adminList(request, model);
    model.addAttribute("faqCategoryList", faqService.getloadFaqCategoryList()); 
   System.out.println("관리자:" + model);
    return "/faq/write";
  }
 
  
  @PostMapping(value="/faq/add.do")
  public String add(HttpServletRequest request, RedirectAttributes redirectAttributes) {
    int addResult = faqService.addFaq(request);
    System.out.println("addResult" + addResult);
    redirectAttributes.addFlashAttribute("addResult", addResult);
    return "redirect:/faq/write.do";
  }
  
  
  @GetMapping(value="/faq/search.do")
  public String search(HttpServletRequest request, Model model) {
    faqService.getSearchList(request, model);
    System.out.println("서치" + request.getParameter("FAQ_CONTENTS"));
    model.addAttribute("customerFaqCategoryList", faqService.getloadFaqCategoryList());
    return "faq/list";
  }
  
  @PostMapping(value="/faq/update.do")
  public String updateList(HttpServletRequest request){
    faqService.modifyList(request);
    return "redirect:/faq/write.do";
  }
  
  @ResponseBody
  @PostMapping(value="/faq/delete.do", produces="application/json")
  public Map<String, Object> deleteFaq(HttpServletRequest request) {
    System.out.println("delete:" + request.getParameter("faqNo"));
    return faqService.deleteFaq(request);
  }
  
  @ResponseBody
  @PostMapping(value="/faq/serchCategory.do" , produces="application/json")
  public Map<String, Object> serchCategory(HttpServletRequest request, Model model) {
    System.out.println("카테고리:" + request.getParameter("faqName"));
    System.out.println("카테고리:" + request.getParameter("category"));
    return faqService.getloadCategorySearchList(request, model);
  } 
  
}