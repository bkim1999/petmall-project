package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InquiryDto {
  private int inquiryNo;
  private String title;
  private String contents;
  private String createdAt;
  private String modifiedAt;
  private String textPw;
  private int checkFlag;
  private int status;
  private int depth;
  private int groupNo;
  private int groupOrder;
  private UserDto userDto;
}