package com.gdu.petmall.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    int display = 10;
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
  
  @Transactional
  @Override
  public boolean addReview(int productNo, ReviewDto review, MultipartHttpServletRequest multipartRequest) throws Exception {
    int addReviewResult = reviewMapper.insertProductReview(review);
    reviewMapper.updateProductRating(productNo);
    
    LocalDate today = LocalDate.now();
    String imagePath = "/product/" + DateTimeFormatter.ofPattern("yyyy/MM/dd").format(today);
    List<MultipartFile> reviewImages = multipartRequest.getFiles("review_images");
    
    int attachCount = 0;

    if(reviewImages.get(0).getSize() == 0) {
      attachCount = 1;
    } else {
      attachCount = 0;
    }
    
    for(MultipartFile reviewImage : reviewImages) {

      if(reviewImage != null && !reviewImage.isEmpty()) {
        // Add Review thumbnails
        String filesystemName = myFileUtils.getFilesystemName(reviewImage.getOriginalFilename());
        File reviewImageFile = new File(imagePath, filesystemName);
        File thumbnailFile = new File(imagePath, "s_" + filesystemName);
        reviewImage.transferTo(reviewImageFile);
        
        Thumbnails.of(reviewImageFile)
                  .size(400, 400)      // 가로 400px, 세로 400px
                  .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
                  .toFile(thumbnailFile);
        ProductImageDto thumbnailImage = ProductImageDto.builder()
            .imageCode("review_" + review.getReviewNo())
            .position("thumbnail")
            .path(imagePath)
            .filesystemName("s_" + filesystemName)
            .build();

        attachCount += productMapper.insertProductImage(thumbnailImage);
        
      }
      
    }
    
    return (addReviewResult == 1) && (reviewImages.size() == attachCount);
      
  }
  
  @Override
  public Map<String, Object> loadNotReviewedList(HttpServletRequest request) {
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    int notReviewedCount = reviewMapper.getNotReviewedCount(userNo);
    int display = 10;
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
  
  
}
