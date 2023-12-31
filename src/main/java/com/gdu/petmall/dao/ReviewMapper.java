package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.ProductOptionDto;
import com.gdu.petmall.dto.ReviewDto;

@Mapper
public interface ReviewMapper {
  public int getProductReviewCount(int productNo);
  public List<ReviewDto> getProductReviewList(Map<String, Object> map);
  public int getUserReviewCount(int userNo);
  public List<ReviewDto> getUserReviewList(Map<String, Object> map);
  public List<ProductOptionDto> getProductOrderList(Map<String, Object> map);
  public int updateProductRating(int productNo);
  public int getNotReviewedCount(int userNo);
  public List<ReviewDto> getNotReviewedList(Map<String, Object> map);
  public int insertProductReview(ReviewDto review);
  public int deleteProductReview(int reviewNo);
  public int updateProductReview(ReviewDto review);
}
