package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import reactor.netty.http.server.HttpServerResponse;

public interface CartService {
  
  public void addCart(HttpServletRequest request, Model model);
  public void getList(HttpServletRequest request, Model model);
  public Map<String, Object>deleteCart(HttpServletRequest request);
  public Map<String, Object>modifyCart(HttpServletRequest request);
                                     
  
}
