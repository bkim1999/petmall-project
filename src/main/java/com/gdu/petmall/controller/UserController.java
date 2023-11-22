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
		
		// 이전 주소 저장되는 요청 Header  값
    String referer = request.getHeader("referer");
    
    // 이전 페이지가 아닌 메인으로 되돌릴 url 
    String[] exceptUrl = {"/join.form","/join_option.form","find_id.form","change_pw.form"};
    String ret="";
    
    
    if(referer!=null) {
    	
    			for(String url:exceptUrl) {
    			
    	// 강제로 메인으로 되돌릴 url
    					if(referer.contains(url)) {
    					ret =request.getContextPath()+"/main.do";
    					}

    			}

    	}else {
     // 이전 페이지 값이 없어서 메인으로 되돌림
    	ret=request.getContextPath()+"/main.do";
    	}
    
    // ret 값이 들어있지 않으면 referer, ret에 값이 있으면 ret 저장
    model.addAttribute("referer", ret.isEmpty() ? referer : ret);
    
    //model.addAttribute("referer", referer == null ? request.getContextPath() + "/main.do" : referer);
    
    //네이버 간편 로그인 방식 선택시
    model.addAttribute("naverLoginURL", userService.getNaverLoginURL(request));
    
		return"user/login";
	}
	
	
	
 // 회원가입 방식 선택 폼으로 이동
	@GetMapping(value = "/join_option.form")
	public String joinOption(HttpServletRequest request, Model model)throws Exception {
		
		
	// 네이버 간편 로그인 (가입)	
	 model.addAttribute("naverLoginURL", userService.getNaverLoginURL(request));
		
		return"user/join_option";
	}
	
 //회원가입폼 으로 이동
	@GetMapping("/join.form")
	public String joinForm() {
		
		return "user/join";
	}
	
	
	//마이페이지로 이동
	@GetMapping(value = "/mypage")
	public String myPage() {
		
		return"user/mypage";
	}
	
	//회원정보 수정폼으로 이동
  @GetMapping("/mypage/profile.form")
  public String mypageForm() {
    return "user/profile";
  }
  
  
  //포인트 확인 페이지로 이동
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
  
  
  //비번 찾기 폼
  @GetMapping(value = "/change_pw.form")
  public String findPwForm() {
  	
  	return "user/change_pw";
  }
  
  
  //복원폼
  @GetMapping("/active.form")
  public String activeForm() {
    return "user/active";
  }
  
  
 /* *********************************************8********** */ 
	
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
	
  
  /* * ****************네이버 간편 가입******************* ** */
  
  // 네이버 간편 가입 1--- ( 네이버 로그인 연동 url 생성 ) 
  @PostMapping("/naver/join.do")
  public void naverJoin(HttpServletRequest request, HttpServletResponse response) {
    userService.naverJoin(request, response);
  }
  
 // 네이버 간편 가입 2---( 토큰 발급 요청 )
  @GetMapping("/naver/getAccessToken.do")
  public String getAccessToken(HttpServletRequest request) throws Exception {
  String accessToken = userService.getNaverLoginAccessToken(request);
  return "redirect:/user/naver/getProfile.do?accessToken=" + accessToken;
  }
  
  //네이버 간편가입 3 ---( 네이버 로그인 / 네이버 로그인 후속작업)
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
  /* * *************************************************** ** */ 
  
  
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
  
  
  //포인트 증감차감 테스트(추후에 삭제해야함) 
  @PostMapping(value = "/point.do")
  public void pointTest(HttpServletRequest request) {
 
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
	
}
