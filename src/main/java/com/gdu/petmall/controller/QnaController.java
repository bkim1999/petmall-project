package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.QnaDto;
import com.gdu.petmall.service.QnaService;

import lombok.RequiredArgsConstructor;

@RequestMapping
@RequiredArgsConstructor
@Controller
public class QnaController {

  private final QnaService qnaService;
   
  // 2가지 선택지 페이지 기능으로 이동
  @GetMapping("/qna/list.do")
  public String list() {
    return "qna/list";
  }
  
  // Q&A 작성글 페이지로 이동
  @GetMapping("/qna/write.form")
  public String write() {
    return "qna/write";
  }
  
  // Q&A 작성글 추가
  @PostMapping("/qna/add.do")
  public String add(MultipartHttpServletRequest multipartRequest
                , RedirectAttributes redirectAttributes) throws Exception{
    boolean addResult = qnaService.addQna(multipartRequest);
    redirectAttributes.addFlashAttribute("addResult", addResult);
    return "redirect:/qna/list.do";
  }
  
  // 내가 작성한 Q&A 전체 목록으로 이동
  @GetMapping("/user/myPostList")
  public String myPostList(HttpServletRequest request, Model model) {
	  
      Map<String, Object> resultMap = qnaService.myPostList(request);
      model.addAttribute("myPostList", resultMap.get("myPostList"));
      
      return "user/myPostList";
  }
  
  // Q&A 게시글 삭제
  @PostMapping("/user/remove.do")
  public String remove(@RequestParam(value="qnaNo", required=false, defaultValue="0") int QnaNo
                 ,   RedirectAttributes redirectAttributes) {
      int removeResult = qnaService.removeQna(QnaNo);
      redirectAttributes.addFlashAttribute("removeResult", removeResult);
      return "redirect:/user/myPostList";
  }
  
  // Q&A 문의글 답변 추가 기능
  @PostMapping("/user/qnadetail/addReply.do")
  public String addReply(HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
      int addReplyResult = qnaService.addReply(request, redirectAttributes);
      redirectAttributes.addFlashAttribute("addReplyResult", addReplyResult);
      return "redirect:/user/myPostList";
  }
  
  // Q&A문의글 상세보기 
  @GetMapping("/user/qnadetail.do")
  public String detail(@RequestParam(value = "qnaNo", defaultValue = "0") int qnaNo,HttpServletRequest request, Model model) {
    QnaDto qna = qnaService.getQna(qnaNo);
    model.addAttribute("qna", qna);
    model.addAttribute("groupNo", qna.getGroupNo()); 
    qnaService.loadQna(request, model);
    

    qnaService.loadCommentlist(request, model);
    
    return "user/qnadetail";
  }
  
  // 첨부파일 다운로드 기능
  @GetMapping("user/qnadetail/download.do")
  public ResponseEntity<Resource> download(HttpServletRequest request) {
    return qnaService.download(request);
  }

  
}