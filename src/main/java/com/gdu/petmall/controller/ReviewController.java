package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.ProductImageDto;
import com.gdu.petmall.dto.ReviewDto;
import com.gdu.petmall.service.ProductService;
import com.gdu.petmall.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/review")
@RequiredArgsConstructor
@Controller
public class ReviewController {
  
  private final ReviewService reviewService;
  private final ProductService productService;
  
  
  @ResponseBody
  @GetMapping(value="/getReviewList.do", produces="application/json")
  public Map<String, Object> loadReviewList(HttpServletRequest request){
    return reviewService.loadProductReviewList(request);
  }
  
  @GetMapping(value="/list.do")
  public String list() {
    return "/review/list";
  }
  
  @ResponseBody
  @GetMapping(value="/getMyReviewList.do", produces="application/json")
  public Map<String, Object> loadMyReviewList(HttpServletRequest request){
    return reviewService.loadUserReviewList(request);
  }
  
  @ResponseBody
  @GetMapping(value="/getNotReviewedList.do", produces="application/json")
  public Map<String, Object> loadNotReviewedList(HttpServletRequest request){
    return reviewService.loadNotReviewedList(request);
  }
  
  @PostMapping(value="/addReview.form")
  public String addReviewForm(@RequestParam Map map, Model model) {
    model.addAttribute("map", map);
    return "/review/add_review";
  }
  
  @PostMapping(value="/addReview.do")
  public String addReview(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) throws Exception {
    boolean addReviewResult = reviewService.addReview(multipartRequest);
    redirectAttributes.addFlashAttribute("addReviewResult", addReviewResult);
    return "redirect:/review/list.do";
  }
  
  @PostMapping(value="/removeReview.do")
  public String removeReview(int reviewNo, RedirectAttributes redirectAttributes) {
    int removeReviewResult = reviewService.removeReview(reviewNo);
    redirectAttributes.addFlashAttribute("removeReviewResult", removeReviewResult);
    return "redirect:/review/list.do";
  }
  
  @PostMapping(value="/editReview.form")
  public String editReviewForm(@RequestParam Map<String, Object> map, Model model) {
    ProductImageDto productImageDto = productService.loadProductThumbnail(Integer.parseInt((String) map.get("productNo")));
    String productImagePath = null;
    if(productImageDto != null) {
      productImagePath = productImageDto.getPath() + "/" + productImageDto.getFilesystemName();
    }
    map.put("productImagePath", productImagePath);
    model.addAttribute("map", map);
    return "/review/edit_review";
  }
  
  @PostMapping(value="/editReview.do")
  public String editReview(@ModelAttribute ReviewDto review, MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) throws Exception {
    boolean editReviewResult = reviewService.editReview(review, multipartRequest);
    redirectAttributes.addFlashAttribute("editReviewResult", editReviewResult);
    return "redirect:/review/list.do";
  }
  
  
}
