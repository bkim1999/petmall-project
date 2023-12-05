package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.service.FaqService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class FaqController {

  private final FaqService faqService;
 
  @GetMapping(value="/faq/list.do")
  public String faqList(HttpServletRequest request, Model model) {
    faqService.getFaqList(request, model);
    return "/faq/list";
  }
 
  @GetMapping(value="/faq/write.do")
  public String faqWrite(HttpServletRequest request, Model model) {
    faqService.adminList(request, model);
    return "/faq/write";
  }
  
  @PostMapping(value="/faq/add.do")
  public String add(HttpServletRequest request, RedirectAttributes redirectAttributes) {
    int addResult = faqService.addFaq(request);
    redirectAttributes.addFlashAttribute("addResult", addResult);
    return "redirect:/faq/write.do";
  }
  
  @GetMapping(value="/faq/search.do")
  public String search(HttpServletRequest request, Model model) {
    faqService.getSearchList(request, model);
    return "faq/list";
  }
  
  @PostMapping(value="/faq/update.do")
  public String updateList(HttpServletRequest request){
    faqService.modifyList(request);
    return "redirect:/faq/write.do";
  }
  
  
  
  
  @PostMapping(value="/faq/delete.do", produces = "application/json")
  public String remove(@RequestParam(value="faqNo") int faqNo, RedirectAttributes redirectAttributes) {
    System.out.println("faqNo:" + faqNo);
    
    int removeResult = faqService.removeFaq(faqNo);
    redirectAttributes.addFlashAttribute("removeResult", removeResult);
    return "redirect:/faq/write.do";
  }
  
  
  
  
  

  
}