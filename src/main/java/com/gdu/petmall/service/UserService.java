package com.gdu.petmall.service;

import java.util.List;
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
	public ResponseEntity<Map<String, Object>> changePw(HttpServletRequest request);

	
	
	public void modifyPw(HttpServletRequest request, HttpServletResponse response);
	public void getPoint(HttpServletRequest request, Model model);
	public UserDto getUser(String email);
  public ResponseEntity<Map<String, Object>> checkEmail(String email);
  public ResponseEntity<Map<String, Object>> sendCode(String email);
  
  public void inactiveUserBatch();
  public void active(HttpSession session, HttpServletRequest request, HttpServletResponse response);
  
  public String getNaverLoginURL(HttpServletRequest request) throws Exception;
  public String getNaverLoginAccessToken(HttpServletRequest request) throws Exception;
  public void naverJoin(HttpServletRequest request, HttpServletResponse response);
  public UserDto getNaverProfile(String accessToken) throws Exception; 
  public void naverLogin(HttpServletRequest request, HttpServletResponse response, UserDto naverProfile) throws Exception;

  

  public String getKakaoLoginURL(HttpServletRequest request) throws Exception;
  public void kakaoJoin(HttpServletRequest request, HttpServletResponse response);
  public String getKakaoLoginAccessToken(HttpServletRequest request) throws Exception; 
  public UserDto getKakaoProfile(String accessToken) throws Exception; 
  public void kakaoLogin(HttpServletRequest request, HttpServletResponse response, UserDto kakaoProfile) throws Exception;

  
  public Map<String, Object> editProfile(MultipartHttpServletRequest multipartRequest) throws Exception;
  public Map<String,Object>getProfileImage(HttpServletRequest request);
  public void removeProfileImage(HttpServletRequest request);
}
