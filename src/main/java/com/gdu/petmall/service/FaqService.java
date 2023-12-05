package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

public interface FaqService {
 
  public void getFaqList(HttpServletRequest request, Model model);
  public void adminList(HttpServletRequest request, Model model);
  public void getSearchList(HttpServletRequest request, Model model);
  public int addFaq(HttpServletRequest request);
  public int removeFaq(int faqNo);
  public Map<String, Object> modifyList(HttpServletRequest request);
  
  
  
}
