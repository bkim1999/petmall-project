package com.gdu.petmall.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dao.ProductMapper;
import com.gdu.petmall.dao.ReviewMapper;
import com.gdu.petmall.dto.ProductImageDto;
import com.gdu.petmall.dto.ProductOptionDto;
import com.gdu.petmall.dto.ReviewDto;
import com.gdu.petmall.dto.UserDto;
import com.gdu.petmall.util.MyFileUtils;
import com.gdu.petmall.util.MyPageUtils;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
  
  private final ReviewMapper reviewMapper;
  private final ProductMapper productMapper;
  private final MyPageUtils myPageUtils;
  private final MyFileUtils myFileUtils;
  
  
  public Map<String, Object> loadProductReviewList(HttpServletRequest request) {
    int productNo = Integer.parseInt(request.getParameter("productNo"));
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    int reviewCount = reviewMapper.getProductReviewCount(productNo);
    int display = 5;
    myPageUtils.setPaging(page, reviewCount, display);
    
    opt = Optional.ofNullable(request.getParameter("order"));
    String order = opt.orElse("REVIEW_MODIFIED_AT");
    
    
    Map<String, Object> map = Map.of("productNo", productNo
                                   , "order", order
                                   , "begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd());
    
    List<ReviewDto> reviewList = reviewMapper.getProductReviewList(map);
    return Map.of("reviewList", reviewList
                , "paging", myPageUtils.getAjaxPaging());
  }
  
  @Override
  public Map<String, Object> loadUserReviewList(HttpServletRequest request) {
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    int reviewCount = reviewMapper.getUserReviewCount(userNo);
    int display = 10;
    myPageUtils.setPaging(page, reviewCount, display);
    opt = Optional.ofNullable(request.getParameter("order"));
    String order = opt.orElse("REVIEW_MODIFIED_AT");
    
    Map<String, Object> map = Map.of("userNo", userNo
                                   , "order", order
                                   , "begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd());
    
    List<ReviewDto> reviewList = reviewMapper.getUserReviewList(map);

    return Map.of("reviewList", reviewList
                , "paging", myPageUtils.getAjaxPaging());
  }
  
  @Override
  public Map<String, Object> loadProductOrderList(HttpServletRequest request) {
    Optional<String> opt = Optional.ofNullable(request.getParameter("userNo"));
    System.out.println("opt:" + request.getParameter("userNo"));
    int userNo = Integer.parseInt(opt.orElse("0"));
    Map<String, Object> map = Map.of("userNo", userNo
                                   , "productNo", request.getParameter("productNo"));
    List<ProductOptionDto> productOrderList = reviewMapper.getProductOrderList(map);
    return Map.of("productOrderList", productOrderList);
  }
  
  @Override
  public Map<String, Object> loadNotReviewedList(HttpServletRequest request) {
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    int notReviewedCount = reviewMapper.getNotReviewedCount(userNo);
    int display = 5;
    myPageUtils.setPaging(page, notReviewedCount, display);
    opt = Optional.ofNullable(request.getParameter("order"));
    String order = opt.orElse("ORDER_DATE");
    
    Map<String, Object> map = Map.of("userNo", userNo
        , "order", order
        , "begin", myPageUtils.getBegin()
        , "end", myPageUtils.getEnd());
    
    List<ReviewDto> notReviewedList = reviewMapper.getNotReviewedList(map);
    System.out.println("here!!!: " + notReviewedList);
    return Map.of("notReviewedList", notReviewedList
        , "paging", myPageUtils.getAjaxPaging());
  }
  
  @Transactional
  @Override
  public boolean addReview(MultipartHttpServletRequest multipartRequest) throws Exception {
    
    int userNo = Integer.parseInt(multipartRequest.getParameter("userNo"));
    int optionNo = Integer.parseInt(multipartRequest.getParameter("optionNo"));
    int productNo = Integer.parseInt(multipartRequest.getParameter("productNo"));
    String reviewTitle = multipartRequest.getParameter("reviewTitle");
    String reviewContents = multipartRequest.getParameter("reviewContents");
    int reviewRating = Integer.parseInt(multipartRequest.getParameter("reviewRating"));
    
    ReviewDto review = ReviewDto.builder()
                                .reviewTitle(reviewTitle)
                                .reviewContents(reviewContents)
                                .userDto(UserDto.builder().userNo(userNo).build())
                                .productOptionDto(ProductOptionDto.builder().optionNo(optionNo).build())
                                .reviewRating(reviewRating)
                                .build();
    
    int addReviewResult = reviewMapper.insertProductReview(review);
    int addReviewImageResult = 0;
    reviewMapper.updateProductRating(productNo);
    
    String imagePath = myFileUtils.getProductImagePath();
    MultipartFile reviewImageMulti = multipartRequest.getFile("review_image");
    
    if(reviewImageMulti != null && !reviewImageMulti.isEmpty()) {
      // 리뷰 이미지 추가
      String filesystemName = myFileUtils.getFilesystemName(reviewImageMulti.getOriginalFilename());
      File reviewImageFile = new File(imagePath, filesystemName);
      File resizedImageFile = new File(imagePath, "r_" + filesystemName);
      reviewImageMulti.transferTo(reviewImageFile);
      
      Thumbnails.of(reviewImageFile)
                .size(400, 400)      // 가로 400px, 세로 400px
                .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
                .toFile(resizedImageFile);
      ProductImageDto reviewImageDto = ProductImageDto.builder()
          .imageCode("review_" + review.getReviewNo())
          .position("review")
          .path(imagePath)
          .filesystemName("r_" + filesystemName)
          .build();

      addReviewImageResult = productMapper.insertProductImage(reviewImageDto);
        
    }
    
    return (addReviewResult == 1) && (addReviewImageResult == 1);
      
  }
  
  @Override
  public int removeReview(int reviewNo) {
    int removeReviewResult = reviewMapper.deleteProductReview(reviewNo);
    return removeReviewResult;
  }
  
  @Override
  public boolean editReview(ReviewDto review, MultipartHttpServletRequest multipartRequest) throws Exception {
    int editReviewResult = reviewMapper.updateProductReview(review);
    int productNo = Integer.parseInt(multipartRequest.getParameter("productNo"));
    reviewMapper.updateProductRating(productNo);
    
    int addReviewImageResult = 0;
    String deletedImage = multipartRequest.getParameter("deletedImage");
    if(deletedImage != null) {
      String[] split = deletedImage.split("/");
      productMapper.deleteProductImage(split[split.length - 1]);
    }
    
    String imagePath = myFileUtils.getProductImagePath();
    MultipartFile reviewImageMulti = multipartRequest.getFile("review_image");
    
    if(reviewImageMulti != null && !reviewImageMulti.isEmpty()) {
      // 리뷰 이미지 추가
      String filesystemName = myFileUtils.getFilesystemName(reviewImageMulti.getOriginalFilename());
      File reviewImageFile = new File(imagePath, filesystemName);
      File resizedImageFile = new File(imagePath, "r_" + filesystemName);
      reviewImageMulti.transferTo(reviewImageFile);
      
      Thumbnails.of(reviewImageFile)
                .size(400, 400)      // 가로 400px, 세로 400px
                .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
                .toFile(resizedImageFile);
      ProductImageDto reviewImageDto = ProductImageDto.builder()
          .imageCode("review_" + review.getReviewNo())
          .position("review")
          .path(imagePath)
          .filesystemName("r_" + filesystemName)
          .build();

      addReviewImageResult = productMapper.insertProductImage(reviewImageDto);
    } else {
      addReviewImageResult = 1;
    }
    
    return (editReviewResult == 1) && (addReviewImageResult == 1);
  }
  
}
