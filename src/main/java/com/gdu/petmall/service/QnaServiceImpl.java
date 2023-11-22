   package com.gdu.petmall.service;
   
   import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dao.QnaMapper;
import com.gdu.petmall.dto.ProductDto;
import com.gdu.petmall.dto.QattachDto;
import com.gdu.petmall.dto.QnaDto;
import com.gdu.petmall.dto.UserDto;
import com.gdu.petmall.util.MyFileUtils;
import com.gdu.petmall.util.MyPageUtils;

import lombok.RequiredArgsConstructor;
   
   @Transactional
   @RequiredArgsConstructor
   @Service
   public class QnaServiceImpl implements QnaService {
   
       private final QnaMapper qnaMapper;
       private final MyFileUtils myFileUtils;
       private final MyPageUtils myPageUtils;
   
       // 게시글 추가
       @Override
       public boolean addQna(MultipartHttpServletRequest multipartRequest) throws Exception {
           int title = 0;
           String titleString = multipartRequest.getParameter("title");
           if (titleString != null && !titleString.isEmpty()) {
               title = Integer.parseInt(titleString);
           }

           String contents = multipartRequest.getParameter("contents");
           String textPw = multipartRequest.getParameter("textPw");

           int userNo = 0;
           String userNoString = multipartRequest.getParameter("userNo");
           if (userNoString != null && !userNoString.isEmpty()) {
               userNo = Integer.parseInt(userNoString);
           }

           int productNo = 0;
           String productNoString = multipartRequest.getParameter("productNo");
           if (productNoString != null && !productNoString.isEmpty()) {
               productNo = Integer.parseInt(productNoString);
           }

           UserDto userDto = UserDto.builder()
                   .userNo(userNo)
                   .build();

           QnaDto qna = QnaDto.builder()
                  .title(title)
                  .contents(contents)
                  .textPw(textPw)
                  .userDto(userDto) 
                  .productNo(productNo)
                  .build();

           qnaMapper.insertQna(qna);
           
           int generatedQnaNo = qna.getQnaNo();
           
           List<MultipartFile> files = multipartRequest.getFiles("files");

           for (MultipartFile multipartFile : files) {
               if (multipartFile != null && !multipartFile.isEmpty()) {
                   String path = myFileUtils.getUploadPath();
                   File dir = new File(path);
                   if (!dir.exists()) {
                       dir.mkdirs();
                   }

                   String originalFilename = multipartFile.getOriginalFilename();
                   String filesystemName = myFileUtils.getFilesystemName(originalFilename);
                   File file = new File(dir, filesystemName);

                   multipartFile.transferTo(file);

                   QattachDto attach = QattachDto.builder()
                           .path(path)
                           .originalFilename(originalFilename)
                           .filesystemName(filesystemName)
                           .qnaNo(generatedQnaNo)
                           .build();

                   qnaMapper.insertQattach(attach);
               }
           }

           return true;
       }

       // 유저번호값 null방지를 위한 변환
       @Override
       public int getLoggedInUserNo(HttpServletRequest request) {
           HttpSession session = request.getSession();
           if (session.getAttribute("user") != null) {
               UserDto loggedInUser = (UserDto) session.getAttribute("user");
               return loggedInUser.getUserNo();
           } else {
               return -1;
           }
       }
       
       // 작성글 목록 조회
       @Override
       public Map<String, Object> myPostList(HttpServletRequest request) {
    	   
    	   int qnaTotalCount = qnaMapper.qnaTotalCount();
    	   request.setAttribute("qnaTotalCount", qnaTotalCount);
           String loggedInUserNo = String.valueOf(getLoggedInUserNo(request));
         
           Map<String, Object> paramMap = new HashMap<>();
           paramMap.put("userNo", loggedInUserNo);

           List<QnaDto> myPostList = qnaMapper.getMyPostList(paramMap);

           Map<String, Object> resultMap = new HashMap<>();
           resultMap.put("myPostList", myPostList);

           return resultMap;
       }
       
       // 전채 기능조회
       @Override
       public QnaDto getQna(int qnaNo) {
           return qnaMapper.getQna(qnaNo);
       }
       
       // 게시글 삭제
       @Override
       public int removeQna(int qnaNo) {
          return qnaMapper.deleteQna(qnaNo);
       }
       
       // 댓글 추가
       @Override
       public int addReply(HttpServletRequest request, RedirectAttributes redirectAttributes) {
           int userNo = getLoggedInUserNo(request);
           int depth = Integer.parseInt(request.getParameter("depth"));
           int groupNo = Integer.parseInt(request.getParameter("groupNo"));

           String contents = request.getParameter("contents");

           qnaMapper.getQna(groupNo);

           int replyDepth = depth + 1;

           QnaDto reply = QnaDto.builder()
                   .contents(contents)
                   .depth(replyDepth)
                   .userDto(UserDto.builder()
                           .userNo(userNo)
                           .build())
                   .groupNo(groupNo)  
                   .build();
           int addReplyResult = qnaMapper.insertReply(reply);
           return addReplyResult;
       }
       
       // 첨부파일 다운로드
       @Override
       public ResponseEntity<Resource> download(HttpServletRequest request) {
         
         // 첨부 파일의 정보 가져오기
         int qattachNo = Integer.parseInt(request.getParameter("qattachNo"));
         QattachDto attach = qnaMapper.getQattach(qattachNo);
         
         // 첨부 파일 File 객체 -> Resource 객체
         File file = new File(attach.getPath(), attach.getFilesystemName());
         Resource resource = new FileSystemResource(file);
         
         // 첨부 파일이 없으면 다운로드 취소
         if(!resource.exists()) {
           return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
         }
         
         // 사용자가 다운로드 받을 파일의 이름 결정 (User-Agent값에 따른 인코딩 처리)
         String originalFilename = attach.getOriginalFilename();
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
       
       // 게시글 및 첨부리스트 조회
       @Override
       public void loadQna(HttpServletRequest request, Model model) {
         Optional<String> opt = Optional.ofNullable(request.getParameter("qnaNo"));
         int qnaNo = Integer.parseInt(opt.orElse("0"));
         
         model.addAttribute("qna", qnaMapper.getQna(qnaNo));
         model.addAttribute("qattachList", qnaMapper.getQattachList(qnaNo));
       }
       
       // 댓글 목록 조회
       @Override
       public void loadCommentlist(HttpServletRequest request, Model model) {
           int qnaNo = Integer.parseInt(request.getParameter("qnaNo"));
           int groupNo = Integer.parseInt(request.getParameter("groupNo"));
         
           Map<String, Object> map = new HashMap<>();
           map.put("qnaNo", qnaNo);
           map.put("groupNo", groupNo);
         
           List<QnaDto> commentList = qnaMapper.getCommentList(map);
         
           Map<String, Object> result = new HashMap<>();
           model.addAttribute("commentList", commentList);
       }

 
}