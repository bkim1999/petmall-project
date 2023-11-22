package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewDto { 
  private int reviewNo;
  private int userNo;
  private int optionNo;
  private String reviewTitle;
  private String reviewContents;
  private double reviewRating; 
  private ProductImageDto reviewImageDto;
}
