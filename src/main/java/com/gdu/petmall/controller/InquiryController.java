package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dao.InquiryMapper;
import com.gdu.petmall.dto.IattachDto;
import com.gdu.petmall.dto.InquiryDto;
import com.gdu.petmall.service.InquiryService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/inquiry")
@RequiredArgsConstructor
@Controller
public class InquiryController {

private final InquiryService inquiryService;
  
  @GetMapping("/list.do")
  public String list(HttpServletRequest request, Model model) {
    inquiryService.getInquiryList(request, model);
    return "inquiry/list";
  }
  
  @GetMapping("/write.form")
  public String write() {
    return "inquiry/write";
  }
  
  @PostMapping("/add.do")
  public String add(MultipartHttpServletRequest multipartHttpServletRequest, 
                    RedirectAttributes redirectAttributes) throws Exception {
    boolean addResult = inquiryService.addInquiry(multipartHttpServletRequest);
    redirectAttributes.addFlashAttribute("addResult", addResult);
    return "redirect:/inquiry/list.do";
  }
  
  @GetMapping("detail.do")
  public String detail(HttpServletRequest request, Model model) { 
    inquiryService.loadInquiry(request, model);
    return "inquiry/detail";
  }
  
  @GetMapping("/download.do")
  public ResponseEntity<Resource> download(HttpServletRequest request) { 
    return inquiryService.download(request);
  }
  
  @GetMapping("/downloadAll.do")
  public ResponseEntity<Resource> downloadAll(HttpServletRequest request) {
    return inquiryService.downloadAll(request);
  }
 
  @GetMapping("edit.form")
  public String edit(@RequestParam(value="inquiryNo", required=false, defaultValue="0") int inquiryNo
                   , Model model) {
    model.addAttribute("inquiry", inquiryService.getInquiry(inquiryNo));
    return "inquiry/edit";
  }
  
 @PostMapping("modify.do")
  public String modify(InquiryDto inquiry, RedirectAttributes redirectAttributes) {
    int modifyResult = inquiryService.modifyInquiry(inquiry);
    redirectAttributes.addFlashAttribute("modifyResult", modifyResult);
    return "redirect:/inquiry/detail.do?inquiryNo=" + inquiry.getInquiryNo();
  }
 
 @ResponseBody
 @PostMapping(value="/addIattach.do", produces="application/json")
 public Map<String, Object> addAttach(MultipartHttpServletRequest multipartRequest) throws Exception {
   return inquiryService.addIattach(multipartRequest);
 }
 
 @ResponseBody
 @GetMapping(value="/getIattachList.do", produces="application/json")
 public Map<String, Object> getIattachList(HttpServletRequest request) {
   return inquiryService.getIattachList(request);
 }
 
 @ResponseBody
 @PostMapping(value="/removeIattach.do", produces="application/json")
 public Map<String, Object> removeIattach(HttpServletRequest request) {
   return inquiryService.removeIattach(request);
 }
 
 @PostMapping("/removeInquiry.do")
 public String removeInquiry(@RequestParam(value="inquiryNo", required=false, defaultValue="0") int inquiryNo
     , RedirectAttributes redirectAttributes) {
   int removeResult = inquiryService.removeInquiry(inquiryNo);
   redirectAttributes.addFlashAttribute("removeResult", removeResult);
   return "redirect:/inquiry/list.do";
 }

}
