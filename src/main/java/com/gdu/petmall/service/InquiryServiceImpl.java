package com.gdu.petmall.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dao.InquiryMapper;
import com.gdu.petmall.dto.IattachDto;
import com.gdu.petmall.dto.InquiryDto;
import com.gdu.petmall.dto.UserDto;
import com.gdu.petmall.util.MyFileUtils;
import com.gdu.petmall.util.MyPageUtils;

import lombok.RequiredArgsConstructor;


@Transactional
@RequiredArgsConstructor
@Service
public class InquiryServiceImpl implements InquiryService {
  
  private final InquiryMapper inquiryMapper;
  private final MyFileUtils myFileUtils;
  private final MyPageUtils myPageUtils;
  
  @Override
  public boolean addInquiry(MultipartHttpServletRequest multipartRequest) throws Exception {
    
    String title = multipartRequest.getParameter("title");
    String contents = multipartRequest.getParameter("contents");
    int userNo = Integer.parseInt(multipartRequest.getParameter("userNo"));
    String textPw = multipartRequest.getParameter("textPw"); 
    int checkFlag = 1;
    int groupNo = 1;
    
    InquiryDto inquiry = InquiryDto.builder()
                        .title(title)
                        .contents(contents)
                        .userDto(UserDto.builder()
                                  .userNo(userNo)
                                  .build())
                        .textPw(textPw)
                        .checkFlag(checkFlag)
                        .groupNo(groupNo)
                        .build();
    
    inquiryMapper.insertInquiry(inquiry);
    
    int inquiryNo =inquiry.getInquiryNo();
 
    List<MultipartFile> files = multipartRequest.getFiles("files");
    
    for(MultipartFile multipartFile : files) {
      
      if(multipartFile != null && !multipartFile.isEmpty()) {
        
        String path = myFileUtils.getUploadPath();
        File dir = new File(path);
        if(!dir.exists()) {
          dir.mkdirs();
        }
        
        String originalFilename = multipartFile.getOriginalFilename();
        String filesystemName = myFileUtils.getFilesystemName(originalFilename);
        File file = new File(dir, filesystemName);
        
        multipartFile.transferTo(file);
      
        IattachDto iattach = IattachDto.builder()
                            .inquiryNo(inquiryNo)               
                            .path(path)
                            .originalFilename(originalFilename)
                            .filesystemName(filesystemName)
                            .build();
        
        inquiryMapper.insertIattach(iattach);
      }
    }
    
    return true;
    
  }
  
  @Transactional(readOnly=true)
  @Override
  public void getInquiryList(HttpServletRequest request, Model model) {
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    int total = inquiryMapper.getInquiryCount();
    int display = 15;
    
    myPageUtils.setPaging(page, total, display);
    
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd());
    
    List<InquiryDto> inquiryList = inquiryMapper.getInquiryList(map);
    model.addAttribute("inquiryList", inquiryList);
    model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/inquiry/list.do"));
    model.addAttribute("beginNo", total - (page - 1) * display);
  }
  
  @Transactional(readOnly=true)
  @Override
  public void loadInquiry(HttpServletRequest request, Model model) {
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("inquiryNo"));
    int inquiryNo = Integer.parseInt(opt.orElse("0"));
                       
    model.addAttribute("inquiry", inquiryMapper.getInquiry(inquiryNo));
    model.addAttribute("iattachList", inquiryMapper.getIattachList(inquiryNo));
    
  }
  
  @Transactional(readOnly=true)
  @Override
  public InquiryDto getInquiry(int inquiryNo) {
    return inquiryMapper.getInquiry(inquiryNo);
  }
  
  @Override
  public Map<String, Object> getIattachList(HttpServletRequest request) {
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("inquiryNo"));
    int inquiryNo = Integer.parseInt(opt.orElse("0"));
    
    return Map.of("iattachList", inquiryMapper.getIattachList(inquiryNo));
  }
  
  @Override
  public ResponseEntity<Resource> download(HttpServletRequest request) {
 
    // 첨부 파일의 정보 가져오기
    int iattachNo = Integer.parseInt(request.getParameter("iattachNo"));
    IattachDto iattach = inquiryMapper.getIattach(iattachNo);

    // 첨부 파일 File 객체 -> Resource 객체
    File file = new File(iattach.getPath(), iattach.getFilesystemName());
    Resource resource = new FileSystemResource(file);
    
    // 첨부 파일이 없으면 다운로드 취소
    if(!resource.exists()) {
      return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
    }
    
    // 사용자가 다운로드 받을 파일의 이름 결정 (User-Agent값에 따른 인코딩 처리)
    String originalFilename = iattach.getOriginalFilename();
    String userAgent = request.getHeader("User-Agent");
    try {
      // IE
      if(userAgent.contains("Trident")) {
        originalFilename = URLEncoder.encode(originalFilename, "UTF-8").replace("+", " ");
      }
      // Edge
      else if(userAgent.contains("Edg")) {
        originalFilename = URLEncoder.encode(originalFilename, "UTF-8");
      }
      // Other
      else {
        originalFilename = new String(originalFilename.getBytes("UTF-8"), "ISO-8859-1");
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    // 다운로드 응답 헤더 만들기
    HttpHeaders header = new HttpHeaders();
    header.add("Content-Type", "application/octet-stream");
    header.add("Content-Disposition", "attachment; filename=" + originalFilename);
    header.add("Content-Length", file.length() + "");
    
    // 응답
    return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
    
  }
  
  @Override
  public ResponseEntity<Resource> downloadAll(HttpServletRequest request) {

    // 다운로드 할 모든 첨부 파일 정보 가져오기
    int inquiryNo = Integer.parseInt(request.getParameter("inquiryNo"));
    List<IattachDto> iattachList = inquiryMapper.getIattachList(inquiryNo);
    
    // 첨부 파일이 없으면 종료
    if(iattachList.isEmpty()) {
      return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
    }
    
    // zip 파일을 생성할 경로
    File tempDir = new File(myFileUtils.getTempPath());
    if(!tempDir.exists()) {
      tempDir.mkdirs();
    }
    
    // zip 파일의 이름
    String zipName = myFileUtils.getTempFilename() + ".zip";
    
    // zip 파일의 File 객체
    File zipFile = new File(tempDir, zipName);
    
    // zip 파일을 생성하는 출력 스트림
    ZipOutputStream zout = null;
    
    // 첨부 파일들을 순회하면서 zip 파일에 등록하기
    try {
      
      zout = new ZipOutputStream(new FileOutputStream(zipFile));
      
      for(IattachDto iattach : iattachList) {
        
        // 각 첨부 파일들의 원래 이름으로 zip 파일에 등록하기 (이름만 등록)
        ZipEntry zipEntry = new ZipEntry(iattach.getOriginalFilename());
        zout.putNextEntry(zipEntry);
        
        // 각 첨부 파일들의 내용을 zip 파일에 등록하기 (실제 파일 등록)
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(new File(iattach.getPath(), iattach.getFilesystemName())));
        zout.write(bin.readAllBytes());
        
        // 자원 반납
        bin.close();
        zout.closeEntry();
        
      }
      
      // zout 자원 반납
      zout.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // 다운로드할 zip 파일의 File 객체 -> Resource 객체
    Resource resource = new FileSystemResource(zipFile);
    
    // 다운로드 응답 헤더 만들기
    HttpHeaders header = new HttpHeaders();
    header.add("Content-Type", "application/octet-stream");
    header.add("Content-Disposition", "attachment; filename=" + zipName);
    header.add("Content-Length", zipFile.length() + "");
    
    // 응답
    return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);

  }
  
  @Override
  public void removeTempFiles() {
    
    File tempDir = new File(myFileUtils.getTempPath());
    File[] targetList = tempDir.listFiles();
    if(targetList != null) {
      for(File target : targetList) {
        target.delete();
      }
    }
    
  }
  
  @Override
  public int modifyInquiry(InquiryDto inquiry) {
    return inquiryMapper.updateInquiry(inquiry);
  }

  @Override
  public Map<String, Object> addIattach(MultipartHttpServletRequest multipartRequest) throws Exception {
    
    List<MultipartFile> files = multipartRequest.getFiles("files");
    
    int iattachCount;
    if(files.get(0).getSize() == 0) {
      iattachCount = 1;
    } else {
      iattachCount = 0;
    }
    
    for(MultipartFile multipartFile : files) {
      
      if(multipartFile != null && !multipartFile.isEmpty()) {
        
        String path = myFileUtils.getUploadPath();
        File dir = new File(path);
        if(!dir.exists()) {
          dir.mkdirs();
        }
        
        String originalFilename = multipartFile.getOriginalFilename();
        String filesystemName = myFileUtils.getFilesystemName(originalFilename);
        File file = new File(dir, filesystemName);
        
        multipartFile.transferTo(file);
      
        
        IattachDto iattach = IattachDto.builder()
                            .inquiryNo(Integer.parseInt(multipartRequest.getParameter("inquiryNo")))               
                            .path(path)
                            .originalFilename(originalFilename)
                            .filesystemName(filesystemName)
                            .build();
        
        iattachCount += inquiryMapper.insertIattach(iattach);
        
      }  
      
    } 
    
    return Map.of("iattachResult", files.size() == iattachCount);
  }
  
  @Override
  public Map<String, Object> removeIattach(HttpServletRequest request) {

    Optional<String> opt = Optional.ofNullable(request.getParameter("iattachNo"));
    int iattachNo = Integer.parseInt(opt.orElse("0"));
    
    
    IattachDto iattach = inquiryMapper.getIattach(iattachNo);
    File file = new File(iattach.getPath(), iattach.getFilesystemName());
    if(file.exists()) {
      file.delete();
    }
    
    int removeResult = inquiryMapper.deleteIattach(iattachNo);
    
    return Map.of("removeResult", removeResult);
    
  }
  
  @Override
  public int removeInquiry(int inquiryNo) {
 
    List<IattachDto> iattachList = inquiryMapper.getIattachList(inquiryNo);
    for(IattachDto iattach : iattachList) {
      
      File file = new File(iattach.getPath(), iattach.getFilesystemName());
      if(file.exists()) {
        file.delete();
      }
      
    }
    
    return inquiryMapper.deleteInquiry(inquiryNo);
  }
  
}
