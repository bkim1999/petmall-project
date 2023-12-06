package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dto.ReviewDto;


public interface ReviewService {
  public Map<String, Object> loadProductReviewList(HttpServletRequest request);
  public Map<String, Object> loadUserReviewList(HttpServletRequest request);
  public Map<String, Object> loadProductOrderList(HttpServletRequest request);
  public Map<String, Object> loadNotReviewedList(HttpServletRequest request);
  public boolean addReview(int productNo, ReviewDto review, MultipartHttpServletRequest multipartRequest) throws Exception;
}
