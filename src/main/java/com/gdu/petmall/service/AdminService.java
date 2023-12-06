package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

public interface AdminService {
  
  public void getQna(HttpServletRequest request, Model model);
  public Map<String, Object> getEvent();
  public Map<String, Object> getAjaxAlllist();
  public Map<String, Object> getAjaxAnswerList(HttpServletRequest request);
  public Map<String, Object> getUser(Model model);
}
