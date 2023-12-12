package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.FaqCategoryDto;
import com.gdu.petmall.dto.FaqDto;

@Mapper
public interface FaqMapper {
  
  public List<FaqCategoryDto> getFaqCategoryList(); 
  
  public List<FaqDto> adminFaqList(Map<String, Object> map);
  public List<FaqDto> customerFaqList(Map<String, Object> map);
  
  public int getSearchCount(Map<String, Object> map);
  public int getSearchCategoryCount(Map<String, Object> map);
  
  public int getFaqCount();
  
  public int deleteFaq(Map<String, Object> map);
  public int insertFaq(FaqDto faq);
  public int updateFaq(FaqDto faq);
  
  
  public List<FaqDto> getSearchCategoryList(Map<String, Object> map);
  public List<FaqDto> getSearchList(Map<String, Object> map);
  public List<FaqDto> getSearchNameList(Map<String, Object> map);
  public List<FaqDto> getSearchTitleList(Map<String, Object> map);
  public List<FaqDto> getSearchContentsList(Map<String, Object> map);
  
  
  
 
  
  
 
  
}