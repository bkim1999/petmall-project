package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dto.UserDto;
import com.gdu.petmall.service.UserService;

import lombok.RequiredArgsConstructor;


@RequestMapping(value="/user")
@RequiredArgsConstructor
@Controller
public class UserController {

  
  private final UserService userService;

  
  //로그인 폼으로 이동
  @GetMapping(value = "/login.form")
  public String loginForm(HttpServletRequest request, Model model) throws Exception{
    
    String referer = request.getHeader("referer");
    
    String[] exceptUrl = {"/join.form","/join_option.form","find_id.form","change_pw.form"};
    String ret="";
    
    
    if(referer!=null) {
      
          for(String url:exceptUrl) {
          
              if(referer.contains(url)) {
              ret =request.getContextPath()+"/main.do";
              }

          }

      }else {
      ret=request.getContextPath()+"/main.do";
      }
    
    model.addAttribute("referer", ret.isEmpty() ? referer : ret);
    
    
    model.addAttribute("naverLoginURL", userService.getNaverLoginURL(request));
    model.addAttribute("kakaoLoginURL",userService.getKakaoLoginURL  (request));
    
    return"user/login";
  }
  
  
  
 // 회원가입 방식 선택 폼으로 이동
  @GetMapping(value = "/join_option.form")
  public String joinOption(HttpServletRequest request, Model model)throws Exception {
    
    
   model.addAttribute("naverLoginURL", userService.getNaverLoginURL(request));
   model.addAttribute("kakaoLoginURL",userService.getKakaoLoginURL(request));
  
    return"user/join_option";
  }
  
 //회원가입폼 
  @GetMapping("/join.form")
  public String joinForm() {
    
    return "user/join";
  }
  
  
  //마이페이지
  @GetMapping(value = "/mypage")
  public String myPage() {
    
    return"user/mypage";
  }
  
  //회원정보 수정
  @GetMapping("/mypage/profile.form")
  public String mypageForm() {
    return "user/profile";
  }
  
  
  //포인트 확인
  @PostMapping(value ={"/point","/mypage/point"})
  public String myPoint(HttpServletRequest request,Model model ) { 
    userService.getPoint(request,model); 
    int userNo=Integer.parseInt(request.getParameter("userNo"));
    return "user/point";
  }
  
  
  //아이디 찾기 폼
  @GetMapping(value = "/find_id.form")
  public String findIdForm() {
    
    return "user/find_id";
  }
  
  
  //비번 찾기
  @GetMapping(value = "/change_pw.form")
  public String findPwForm() {
    
    return "user/change_pw";
  }
  
  
  //복원
  @GetMapping("/active.form")
  public String activeForm() {
    return "user/active";
  }
  

  
  // 로그인 
  @PostMapping(value = "/login.do")
    public void login(HttpServletRequest request, HttpServletResponse response)throws Exception {
    userService.login(request, response);
    }
  
  
  //로그아웃
  @GetMapping("/logout.do")
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    userService.logout(request,response);
  }
  
  //회원가입
  @PostMapping("/join.do")
  public void join(HttpServletRequest request, HttpServletResponse response) {
    userService.join(request, response);
  }
  
  
  //네이버 간편가입
  @PostMapping("/naver/join.do")
  public void naverJoin(HttpServletRequest request, HttpServletResponse response) {
    userService.naverJoin(request, response);
  }
  
  @GetMapping("/naver/getAccessToken.do")
  public String getAccessToken(HttpServletRequest request) throws Exception {
  String accessToken = userService.getNaverLoginAccessToken(request);
  return "redirect:/user/naver/getProfile.do?accessToken=" + accessToken;
  }
  
  @GetMapping("/naver/getProfile.do")
  public String getProfile(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    UserDto naverProfile = userService.getNaverProfile(request.getParameter("accessToken"));
    UserDto user = userService.getUser(naverProfile.getEmail());
    if(user == null) {
      model.addAttribute("naverProfile", naverProfile);
      return "user/naver_join";
    } else {
      userService.naverLogin(request, response, naverProfile);
      return "redirect:/main.do";
    }
  }

  
 
  
 //카카오 간편가입 
  @PostMapping("/kakao/join.do")
  public void kakaoJoin(HttpServletRequest request, HttpServletResponse response) {
    userService.kakaoJoin(request, response);
  }
  @GetMapping("/kakao/getAccessToken.do")
  public String getKakaoAccessToken(HttpServletRequest request) throws Exception {
  String accessToken = userService.getKakaoLoginAccessToken(request);
  return "redirect:/user/kakao/getProfile.do?accessToken=" + accessToken;
  }
  @GetMapping("/kakao/getProfile.do")
  public String getKakaoProfile(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    UserDto kakaoProfile = userService.getKakaoProfile(request.getParameter("accessToken"));
    UserDto user = userService.getUser(kakaoProfile.getEmail());
    if(user == null) {
      model.addAttribute("kakaoProfile", kakaoProfile);
      return "user/kakao_join";
    } else {
      userService.kakaoLogin(request, response, kakaoProfile);
      return "redirect:/main.do";
    }
  }

  
  //회원탈퇴
  @PostMapping("/mypage/leave.do")
  public void leave(HttpServletRequest request, HttpServletResponse response) {
    userService.leave(request, response);
  }
  
  
 //이메일 체크
  @GetMapping(value="/checkEmail.do", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
    return userService.checkEmail(email);
  }
  
  //코드 전송
  @GetMapping(value="/sendCode.do", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> sendCode(@RequestParam String email) {
    return userService.sendCode(email);
  }
  
  //회원정보 수정
  @PostMapping(value="/mypage/modify.do", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> modify(HttpServletRequest request) {
    return userService.modify(request);
  }
  
  
  // 아이디 찾기
  @PostMapping(value = "/find_id.do",produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> findId(HttpServletRequest request) {
    return userService.findId(request);
    
  }
  
  
  // 비밀번호 변경 폼 응답
  @PostMapping(value = "/change_pw.do",produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> changePw(HttpServletRequest request) {
    return userService.changePw(request);
  }
  
  
  // 비밀번호 변경 적용
  @PostMapping(value = "/modify_pw.do")
  public void modifyPw(HttpServletRequest request, HttpServletResponse response) {
     userService.modifyPw(request,response);
  }
  
  
  //휴면복원
  @GetMapping("/active.do")
  public void active(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
    userService.active(session, request, response);
  }
  
  
  //프로필 이미지 첨부 
  @ResponseBody
  @PostMapping(value="/editProfile.do", produces="application/json")
  public Map<String, Object> addAttach(MultipartHttpServletRequest multipartRequest) throws Exception {
   
    return userService.editProfile(multipartRequest);
  }
  
  //프로필 이미지 불러오기
  @ResponseBody
  @GetMapping(value="/getProfileImage.do", produces="application/json")
  public Map<String, Object> getProfileImage(HttpServletRequest request) {
    return userService.getProfileImage(request);
  }

  
  //프로필 이미지 삭제
  @ResponseBody
  @PostMapping(value="/removeProfileImage.do", produces="application/json")
  public void removeProfileImage(HttpServletRequest request) {
  userService.removeProfileImage(request);
  }
  

  
}
