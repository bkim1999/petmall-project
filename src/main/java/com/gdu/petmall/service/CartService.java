package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.gdu.petmall.dto.CartDto;

public interface CartService {
  
  public void addCart(HttpServletRequest request, Model model);
  public void getList(HttpServletRequest request, Model model);
  public Map<String, Object> modifyCart(HttpServletRequest request,CartDto cartDto);
  public Map<String, Object> removeCart(HttpServletRequest request) throws Exception;

  
  
}
