package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.FaqDto;

@Mapper
public interface FaqMapper {

  public int getFaqCount();
  public List<FaqDto> getFaqList(Map<String, Object> map);
  
  public int insertReply(FaqDto faq);
  public int updateGroupOrder(FaqDto faq);

  
  
  
  public int getSearchCount(Map<String, Object> map);
}