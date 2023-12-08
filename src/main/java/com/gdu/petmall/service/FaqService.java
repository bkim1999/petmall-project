package com.gdu.petmall.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.gdu.petmall.dto.FaqCategoryDto;

public interface FaqService {
 
  
  public List<FaqCategoryDto> getloadFaqCategoryList();
  public void customerFaqList(HttpServletRequest request, Model model);
  public void adminList(HttpServletRequest request, Model model);
  public void getSearchList(HttpServletRequest request, Model model);
  public Map<String, Object> deleteFaq(HttpServletRequest request);
  public Map<String, Object> modifyList(HttpServletRequest request);
  public int addFaq(HttpServletRequest request);
  
  
  
  public Map<String, Object> getloadCategorySearchList(HttpServletRequest request, Model model);
  
  
}
