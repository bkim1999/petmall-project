package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

public interface AdminService {
  
  public void getQna(HttpServletRequest request, Model model);
  public Map<String, Object> getEvent();
  public Map<String, Object> getAjaxAlllist();
  public Map<String, Object> getAjaxAnswerList(HttpServletRequest request);
  public Map<String, Object> getUser();
  public Map<String, Object> pwInit(HttpServletRequest request);
  public Map<String, Object> adminTake(HttpServletRequest request);
  public Map<String, Object> normalTake(HttpServletRequest request);
  public Map<String, Object> changeagree(HttpServletRequest request);
  public Map<String, Object> changedeagree(HttpServletRequest request);
  public Map<String, Object> searchInfo(HttpServletRequest request);
  public Map<String, Object> LeaveSearchInfo(HttpServletRequest request);
  public Map<String, Object> UnaccessSearchInfo(HttpServletRequest request);
  public Map<String, Object> accessList();
  public Map<String, Object> LeaveList();
  public Map<String, Object> changeAccessUser(HttpServletRequest request);
  public Map<String, Object> eventCount();
  
}
