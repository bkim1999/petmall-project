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
  public List<ProductOptionDto> getProductOrderList(Map<String, Object> map);
  public int insertProductReview(ReviewDto review);
  public int updateProductRating(int productNo);
}
