package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.ReviewDto;
import com.gdu.petmall.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/review")
@RequiredArgsConstructor
@Controller
public class ReviewController {
  
  private final ReviewService reviewService;
  
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
    System.out.println("here!");
    return reviewService.loadUserReviewList(request);
  }
  
  @GetMapping(value="/addReview.form")
  public String addReviewForm(HttpServletRequest request, Model model) {
    model.addAttribute("userNo", request.getParameter("userNo"));
    model.addAttribute("optionNo", request.getParameter("optionNo"));
    model.addAttribute("productNo", request.getParameter("productNo"));
    model.addAttribute("productName", request.getParameter("productName"));
    model.addAttribute("optionName", request.getParameter("optionName"));
    model.addAttribute("productImagePath", request.getParameter("productImagePath"));
    return "/review/add_review";
  }
  
  @ResponseBody
  @GetMapping(value="/getNotReviewedList.do", produces="application/json")
  public Map<String, Object> loadNotReviewedList(HttpServletRequest request){
    return reviewService.loadNotReviewedList(request);
  }
  
  @PostMapping(value="/addReview.do")
  public String addReview(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) throws Exception {
    boolean addReviewResult = reviewService.addReview(multipartRequest);
    redirectAttributes.addFlashAttribute("addReviewResult", addReviewResult);
    return "redirect:/review/list.do";
  }
  
  
  
}
