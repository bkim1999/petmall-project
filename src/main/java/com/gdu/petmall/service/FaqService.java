package com.gdu.petmall.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

public interface FaqService {
 
  public void getFaqList(HttpServletRequest request, Model model);
  public int addReply(HttpServletRequest request);
}
