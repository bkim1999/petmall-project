package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.IattachDto;
import com.gdu.petmall.dto.InquiryDto;

@Mapper
public interface InquiryMapper {
  public int insertInquiry(InquiryDto inquiry);
  public int insertIattach(IattachDto iattach);
  public int getInquiryCount();
  public List<InquiryDto> getInquiryList(Map<String, Object> map);
  public InquiryDto getInquiry(int inquiryNo);
  public List<IattachDto> getIattachList(int inquiryNo);
  public IattachDto getIattach(int iattachNo);
  public int updateInquiry(InquiryDto inquiry);
  public int deleteIattach(int iattachNo);
  public int deleteInquiry(int inquiryNo);
}
