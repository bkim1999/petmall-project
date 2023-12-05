package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface EventService {
  
  public void loadEventList(HttpServletRequest request, Model model);
  public void loaddetailEventList(int eventNo, Model model);
  public int increaseHit(int eventNo);
  public void addEvent(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes, HttpServletRequest request) throws  Exception;
  public Map<String, Object> eventImageUpload(MultipartHttpServletRequest multipartRequest);
  public Map<String, Object> endEvent(HttpServletRequest request);
  public Map<String, Object> startEvent(HttpServletRequest request);
  public Map<String, Object> changePercent(HttpServletRequest request);
  public Map<String, Object> changePrice(HttpServletRequest request);
  public void updateDetailEvent(MultipartHttpServletRequest multipartRequest, Model model) throws Exception;
  public void eventImageBatch();
  public void eventAutoEnd();
  public void eventAutoStart();
  
}
