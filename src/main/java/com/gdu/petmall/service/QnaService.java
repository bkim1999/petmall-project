package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.QnaDto;

public interface QnaService {

  // 게시글 추가
  public boolean addQna(MultipartHttpServletRequest multipartRequest) throws Exception;
  
  // 유저 고유값 변환
  public int getLoggedInUserNo(HttpServletRequest request);
  
  // 게시글 나의글 한번에 몰아보기 목록
  public Map<String, Object> myPostList(HttpServletRequest request);
  
  // Q&A 사용하는 전체 세팅
  public QnaDto getQna(int QnaNo);
  
  // 특정 게시글 삭제하기
  public int removeQna(int qnaNo);
  
  // 댓글 기능추가
  public int addReply(HttpServletRequest request,  RedirectAttributes redirectAttributes) ;
  
  // 첨부파일 목록보기 
  public void loadQna(HttpServletRequest request, Model model);
  
  // 첨부파일 다운로드 
  public ResponseEntity<Resource> download(HttpServletRequest request);
  
  // 답변 한번에 모아보기 
  public void loadCommentlist(HttpServletRequest request, Model model); 
}