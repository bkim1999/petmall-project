package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

public interface CartService {
  
  public void addCart(HttpServletRequest request, Model model);
  public void getList(HttpServletRequest request, Model model);
  public Map<String, Object>deleteCart(HttpServletRequest request);
  
  
  public Map<String, Object>minusCart(HttpServletRequest request);
  public Map<String, Object>plusCart(HttpServletRequest request);                                   
  
}
