package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.CategoryDto;
import com.gdu.petmall.dto.FaqCategoryDto;
import com.gdu.petmall.dto.FaqDto;

@Mapper
public interface FaqMapper {
  
  public List<FaqCategoryDto> getFaqCategoryList();
  public List<CategoryDto> getCategoryList();
  public List<FaqDto> getFaqList(Map<String, Object> map);
  public List<FaqDto> getSearchList(Map<String, Object> map);
  public List<FaqDto> adminFaqList(Map<String, Object> map);
  public int getSearchCount(Map<String, Object> map);
  public int getFaqCount();
  public int deleteFaq(int faqNo);
  public int insertFaq(FaqDto faq);
  public int updateFaq(FaqDto faq);
  
  
 
  
}