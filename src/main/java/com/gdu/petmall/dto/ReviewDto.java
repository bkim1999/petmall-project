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
  private String reviewTitle;
  private String reviewContents;
  private int reviewRating;
  private String reviewCreatedAt;
  private String reviewModifiedAt;
  private UserDto userDto;                      // 리뷰목록보기용 DB엔 userNo만 있음
  private ProductOptionDto productOptionDto;    // 리뷰목록보기용 DB엔 optionNo만 있음
  private ProductDto productDto;                // 리뷰목록보기용 DB엔 없음
  private ProductImageDto reviewImageDto;       // 리뷰목록보기용 DB엔 없음
  private String orderDate;                     // 리뷰작성용 DB엔 없음
}
