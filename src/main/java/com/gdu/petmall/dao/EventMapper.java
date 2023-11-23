package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.EventDto;
import com.gdu.petmall.dto.EventImageDto;

@Mapper
public interface EventMapper {
  
  public List<EventDto> getEventList(Map<String, Object> map);
  public int getEventCount();
  public EventDto getEventDetailList(int eventNo);
  public int updateHit(int eventNo);
  public int insertEventWrite(EventDto eventDto);
  public int insertEventImage(EventImageDto eventImageDto);
  public List<EventDto> getTotalList();
  public int eventEnd(int eventNo);
  public int eventStart(int eventNo);
  public int changeDiscountPercent(Map<String, Object> map);
  public int changeDiscountPrice(Map<String, Object> map);
}
