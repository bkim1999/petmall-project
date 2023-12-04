package com.gdu.petmall.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
  
  @PostMapping(value="/faq/addReply.do")
  public String addReply(HttpServletRequest request, RedirectAttributes redirectAttributes) {
    int addReplyResult = faqService.addReply(request);
    redirectAttributes.addFlashAttribute("addReplyResult", addReplyResult);
    return "redirect:/faq/list.do";
  }
  
}