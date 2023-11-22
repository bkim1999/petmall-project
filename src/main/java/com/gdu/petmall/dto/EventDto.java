package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EventDto {
  
  private int eventNo;
  private String title;
  private String contents;
  private int hit;
  private int state;
  private String eventThumnailUrl;
  private int discountPercent;
  private int discountPrice;
  private String startAt;
  private String endAt;
  private EventImageDto eventImageDto;

}
