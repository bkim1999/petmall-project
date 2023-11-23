package com.gdu.petmall.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dto.IattachDto;
import com.gdu.petmall.dto.InquiryDto;

public interface InquiryService {
  
  public boolean addInquiry(MultipartHttpServletRequest multipartRequest) throws Exception;

  public void getInquiryList(HttpServletRequest request, Model model);
  
  public void loadInquiry(HttpServletRequest request, Model model);
  
  public InquiryDto getInquiry(int inquiryNo);
  
  public Map<String, Object> getIattachList(HttpServletRequest request);
  
  public ResponseEntity<Resource> download(HttpServletRequest request);
  
  public ResponseEntity<Resource> downloadAll(HttpServletRequest request);

  public void removeTempFiles();
  
  public int modifyInquiry(InquiryDto inquiry);
  
  public Map<String, Object> addIattach(MultipartHttpServletRequest multipartRequest) throws Exception;
  
  public Map<String, Object> removeIattach(HttpServletRequest request);
  
  public int removeInquiry(int inquiryNo);
  
}
