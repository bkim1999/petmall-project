package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dto.UserDto;

public interface UserService {
	
  public void login(HttpServletRequest request, HttpServletResponse response) throws Exception;
	public void logout(HttpServletRequest request,HttpServletResponse response);
	public void join(HttpServletRequest request, HttpServletResponse response);
	public void leave(HttpServletRequest request, HttpServletResponse response);
	public ResponseEntity<Map<String, Object>> modify(HttpServletRequest request);
	public ResponseEntity<Map<String, Object>> findId(HttpServletRequest request);
	public ResponseEntity<Map<String, Object>> changePw(HttpServletRequest request);//비번변경폼에서 이메일일치여부 확인
	
	
	public void modifyPw(HttpServletRequest request, HttpServletResponse response);//비번 변경 적용
	public void getPoint(HttpServletRequest request, Model model);
	public UserDto getUser(String email);
  public ResponseEntity<Map<String, Object>> checkEmail(String email);
  public ResponseEntity<Map<String, Object>> sendCode(String email);
  
  /*휴면 처리 */
  public void inactiveUserBatch();
  /*휴면 복원 */
  public void active(HttpSession session, HttpServletRequest request, HttpServletResponse response);
  
  /*네이버 api관련*/
  public String getNaverLoginURL(HttpServletRequest request) throws Exception;// 네이버로그인 url 가져와
  public String getNaverLoginAccessToken(HttpServletRequest request) throws Exception; //인증토큰 가져와
  public void naverJoin(HttpServletRequest request, HttpServletResponse response);//네이버 간편가입
  public UserDto getNaverProfile(String accessToken) throws Exception; // 네이버 로그인 후속작업
  public void naverLogin(HttpServletRequest request, HttpServletResponse response, UserDto naverProfile) throws Exception;//네이버로그인

  
  /*카카오 api 관련*/
  public String getKakaoLoginURL(HttpServletRequest request) throws Exception;//  카카오 로그인 url 가져와
  public void kakaoJoin(HttpServletRequest request, HttpServletResponse response);//카카오 간편가입
  public String getKakaoLoginAccessToken(HttpServletRequest request) throws Exception; //인증토큰 가져와
  public UserDto getKakaoProfile(String accessToken) throws Exception; // 카카오 로그인 후속작업
  public void kakaoLogin(HttpServletRequest request, HttpServletResponse response, UserDto kakaoProfile) throws Exception;//카카오로그인

  
  /*프로필 이미지 첨부 관련*/
  public Map<String, Object> editProfile(MultipartHttpServletRequest multipartRequest) throws Exception;// 첨부
  public Map<String,Object>getProfileImage(HttpServletRequest request);
}
