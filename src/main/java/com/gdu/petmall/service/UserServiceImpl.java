package com.gdu.petmall.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dao.AutoLoginMapper;
import com.gdu.petmall.dao.ProfileMapper;
import com.gdu.petmall.dao.UserMapper;
import com.gdu.petmall.dto.AutoLoginDto;
import com.gdu.petmall.dto.InactiveUserDto;
import com.gdu.petmall.dto.ProfileDto;
import com.gdu.petmall.dto.UserDto;
import com.gdu.petmall.util.MyFileUtils;
import com.gdu.petmall.util.MyJavaMailUtils;
import com.gdu.petmall.util.MySecurityUtils;

import lombok.RequiredArgsConstructor;


@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	
	private final UserMapper userMapper;
	private final MySecurityUtils mySecurityUtils;
	private final MyJavaMailUtils myJavaMailUtils;
	private final ProfileMapper profileMapper;
	private final MyFileUtils myFileUtils;
	private final AutoLoginMapper autoLoginMapper;

	//네이버
  private final String client_id = "OsFLRfpdkM0BZr7yfZrr";
  private final String client_secret = "eiyecu8MwC";
  //카카오
  private final String kakao_api_key="c3db793b2723f0cd2a96e05470dbdc6f";
  private final String kakao_secret_key="i1Bd2PWwfhLAgkJ93YkXdYaf2v5UZc60";
	
@Override
public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
	 
 
	
  String email = mySecurityUtils.preventXSS(request.getParameter("email"));
  String pw = mySecurityUtils.getSHA256(request.getParameter("pw")); 
  String isAutoLoginActive=request.getParameter("auto_login");
  
  Map<String, Object> map = Map.of("email", email
                                 , "pw", pw);
	
  
  HttpSession session = request.getSession();

  InactiveUserDto inactiveUser = userMapper.getInactiveUser(map);
  if(inactiveUser != null) {
    session.setAttribute("inactiveUser", inactiveUser);
    response.sendRedirect(request.getContextPath() + "/user/active.form"); // 계정 활성페이지로 보내
  }
  
  UserDto user = userMapper.getUser(map);
  
  
  if(isAutoLoginActive!=null)
  {
	   Cookie userCookie=new Cookie("userCookie", session.getId());
	   
	   	userCookie.setPath("/");
	    userCookie.setMaxAge(60*60*24*15); 
	    response.addCookie(userCookie);
	    int userNo=user.getUserNo();
	      
	    AutoLoginDto autoLoginDto=AutoLoginDto.builder()
	    									 .email(email)
	    									 .pw(pw)
	    									 .token(userCookie.getValue())
	    									 .userNo(userNo)
	    									 .build();
 
	    autoLoginMapper.saveAutoLoginToken(autoLoginDto);      
  }
 
  
  
  if(user != null) {				
    request.getSession().setAttribute("user", user);
    userMapper.insertAccess(email);
    response.sendRedirect(request.getParameter("referer"));
  } else {
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    out.println("alert('일치하는 회원 정보가 없습니다.')");
    out.println("location.href='" + request.getContextPath() + "/main.do'");
    out.println("</script>");
    out.flush();
    out.close();
    
  }
}


@Override
public UserDto getUser(String email) {
  return userMapper.getUser(Map.of("email", email));
}


@Override
public void logout(HttpServletRequest request, HttpServletResponse response) {

HttpSession session=request.getSession();
UserDto user = (UserDto) session.getAttribute("user");
autoLoginMapper.deleteAutoLoginToken(user.getEmail());
deleteCookie(response);
session.invalidate();
try {
	response.sendRedirect(request.getContextPath()+"/main.do");
	
}catch (Exception e) {
e.printStackTrace();
}

}

public void deleteCookie(HttpServletResponse response){
    Cookie dropCookie = new Cookie("userCookie", null); 
    dropCookie.setPath("/");
    dropCookie.setMaxAge(0); 
    response.addCookie(dropCookie); 
}

@Transactional(readOnly=true)
@Override
public ResponseEntity<Map<String, Object>> checkEmail(String email) {
  
  Map<String, Object> map = Map.of("email", email);
  
  boolean enableEmail = userMapper.getUser(map) == null
                     && userMapper.getLeaveUser(map) == null
                     && userMapper.getInactiveUser(map) == null;
  
  return new ResponseEntity<>(Map.of("enableEmail", enableEmail), HttpStatus.OK);
  
}

@Override
public ResponseEntity<Map<String, Object>> sendCode(String email) {
  String code = mySecurityUtils.getRandomString(8, true, true);
  myJavaMailUtils.sendJavaMail(email
                             , "petmall 인증 코드"
                             , "<div>인증코드는 <strong>" + code + "</strong>입니다.</div>");
  
  return new ResponseEntity<>(Map.of("code", code), HttpStatus.OK);
  
}


@Override
public void join(HttpServletRequest request, HttpServletResponse response) {

  String email = request.getParameter("email");
  String pw = mySecurityUtils.getSHA256(request.getParameter("pw"));
  String name = mySecurityUtils.preventXSS(request.getParameter("name"));
  String gender = request.getParameter("gender");
  
  String[] arrMobile = request.getParameterValues("mobile");
  StringBuilder mobile =new StringBuilder();
  for(int i=0;i<arrMobile.length;i++){mobile.append(arrMobile[i]);}
  
  String postcode = request.getParameter("postcode");
  String roadAddress = request.getParameter("roadAddress");
  String jibunAddress = request.getParameter("jibunAddress");
  String detailAddress = mySecurityUtils.preventXSS(request.getParameter("detailAddress"));
  
  
  
  String event = request.getParameter("event");
  Optional<String> opt = Optional.ofNullable(event);
   event = opt.orElse("off"); 
  
  
  int adminAuthorState=Integer.parseInt(request.getParameter("admin_author_state"));
  
  
  UserDto user = UserDto.builder()
      .email(email)
      .pw(pw)
      .name(name)
      .gender(gender)
      .mobile(mobile.toString())
      .postcode(postcode)
      .roadAddress(roadAddress)
      .jibunAddress(jibunAddress)
      .detailAddress(detailAddress)
      .agree(event.equals("on") ? 1 : 0)
      .adminAuthorState(adminAuthorState)
      .build();
  
  if(userMapper.getEmailResult(email)!=0||userMapper.getEmailResultInactive(email)!=0)
  {
    
    try {
      
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      out.println("alert('이미 가입된 이메일입니다.')");
      out.println("history.go(-1)");
      out.println("</script>");
      out.flush();
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  else {
  
  
  int joinResult = userMapper.insertUser(user);
  
  

   try {
    
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    if(joinResult == 1) {
      request.getSession().setAttribute("user", userMapper.getUser(Map.of("email", email)));
      userMapper.insertAccess(email);
      out.println("alert('회원 가입되었습니다.')");
      out.println("location.href='" + request.getContextPath() + "/main.do'");
    } else {
      out.println("alert('회원 가입이 실패했습니다.')");
      out.println("history.go(-2)");
    }
    out.println("</script>");
    out.flush();
    out.close();
    
  } catch (Exception e) {
    e.printStackTrace();
  }
  
  }
}


@Override
public void leave(HttpServletRequest request, HttpServletResponse response) {

  Optional<String> opt = Optional.ofNullable(request.getParameter("userNo"));
  int userNo = Integer.parseInt(opt.orElse("0"));
  
  UserDto user = userMapper.getUser(Map.of("userNo", userNo));
  
  if(user == null) {
    try {
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      out.println("alert('회원 탈퇴를 수행할 수 없습니다.')");
      out.println("location.href='" + request.getContextPath() + "/main.do'");
      out.println("</script>");
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  int insertLeaveUserResult = userMapper.insertLeaveUser(user);
  int deleteUserResult = userMapper.deleteUser(user);
  
 try {
    
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    if(insertLeaveUserResult == 1 && deleteUserResult == 1) {
      HttpSession session = request.getSession();
      session.invalidate();
      out.println("alert('회원 탈퇴되었습니다. 그 동안 이용해 주셔서 감사합니다.')");
      out.println("location.href='" + request.getContextPath() + "/main.do'");
    } else {
      out.println("alert('회원 탈퇴되지 않았습니다.')");
      out.println("history.back()");
    }
    out.println("</script>");
    out.flush();
    out.close();
    
  } catch (Exception e) {
    e.printStackTrace();
  }
  
}
	

@Override
public ResponseEntity<Map<String, Object>> modify(HttpServletRequest request) {
	  String email = mySecurityUtils.preventXSS(request.getParameter("email"));
	  String pw = mySecurityUtils.getSHA256(request.getParameter("pw"));
	  String name = mySecurityUtils.preventXSS(request.getParameter("name"));
	  String gender = request.getParameter("gender");
	  
	  String[] arrMobile = request.getParameterValues("mobile");
	  StringBuilder mobile =new StringBuilder();
	  for(int i=0;i<arrMobile.length;i++){mobile.append(arrMobile[i]);}
	  
	  String postcode = request.getParameter("postcode");
	  String roadAddress = request.getParameter("roadAddress");
	  String jibunAddress = request.getParameter("jibunAddress");
	  String detailAddress = mySecurityUtils.preventXSS(request.getParameter("detailAddress"));
	  
	  int userNo = Integer.parseInt(request.getParameter("userNo"));
	  
	  
	  
	  String event = request.getParameter("event");
	  Optional<String> opt = Optional.ofNullable(event);
	   event = opt.orElse("off"); 
	   
	  int agree = event.equals("on") ? 1 : 0;
	  
	  
	  
	  
	  UserDto user = UserDto.builder()
	      .email(email)
	      .pw(pw)
	      .name(name)
	      .gender(gender)
	      .mobile(mobile.toString())
	      .postcode(postcode)
	      .roadAddress(roadAddress)
	      .jibunAddress(jibunAddress)
	      .detailAddress(detailAddress)
	      .agree(agree)
	      .userNo(userNo)
	      .build();
	  
   int modifyResult = userMapper.updateUser(user);
   
   if(modifyResult == 1) {
     HttpSession session = request.getSession();
     UserDto sessionUser = (UserDto)session.getAttribute("user");
     sessionUser.setEmail(email);
     sessionUser.setPw(pw);
     sessionUser.setName(name);
     sessionUser.setGender(gender);
     sessionUser.setMobile(mobile.toString());
     sessionUser.setPostcode(postcode);
     sessionUser.setRoadAddress(roadAddress);
     sessionUser.setJibunAddress(jibunAddress);
     sessionUser.setDetailAddress(detailAddress);
     sessionUser.setAgree(agree);
   }
   
   return new ResponseEntity<>(Map.of("modifyResult", modifyResult), HttpStatus.OK);
}



@Override
public void getPoint(HttpServletRequest request, Model model) {

	int userNo=Integer.parseInt(request.getParameter("userNo"));
	Map<String,Object> map=Map.of("userNo",userNo);
	
	int  point=userMapper.getPoint(map);
	
	model.addAttribute("point",point);
	model.addAttribute("userNo",userNo);
	
}


@Override
public ResponseEntity<Map<String, Object>> findId(HttpServletRequest request) {
	
	String name= mySecurityUtils.preventXSS(request.getParameter("name"));
	String mobile= mySecurityUtils.preventXSS(request.getParameter("mobile"));
	
	String email= userMapper.getEmail(Map.of("name",name,"mobile",mobile));
	
	if(email!=null)
	{
		return new ResponseEntity<>(Map.of("email", email), HttpStatus.OK);
	}
	else {
		return new ResponseEntity<>(Map.of("email", 0), HttpStatus.OK);
	}
	
	
}


@Override
public ResponseEntity<Map<String, Object>> changePw(HttpServletRequest request) {
	
	
	String email=mySecurityUtils.preventXSS(request.getParameter("email"));	
	
	
	
	if(userMapper.getEmailforPw(Map.of("email",email))==1) {
		
		return new ResponseEntity<>(Map.of("result", 1), HttpStatus.OK);
		
	}else {
		return new ResponseEntity<>(Map.of("result", 0), HttpStatus.OK);
	}	

}

@Override
public void modifyPw(HttpServletRequest request, HttpServletResponse response) {

	String email=mySecurityUtils.preventXSS(request.getParameter("valid_email"));
	String pw=mySecurityUtils.getSHA256(request.getParameter("pw"));
	
	UserDto user=UserDto.builder()
											.email(email)
											.pw(pw)
											.build();
											
	
	int updateResult=userMapper.updatePw(user);
	
	try {
		
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out=response.getWriter();
		out.println("<script>");
		if(updateResult==1)
		{
			out.println("alert('비밀번호가 변경되었습니다.')");
			out.println("location.href='"+request.getContextPath()+"/main.do'");
		}
		else {
			out.println("alert('비밀번호 변경이 실패했습니다.')");
			out.println("history.back()");
		}
		
		out.println("</script>");
		 out.flush();
     out.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	

}

@Override
public void naverJoin(HttpServletRequest request, HttpServletResponse response) {
  
  String email = request.getParameter("email");
  String name = request.getParameter("name");
  String gender = request.getParameter("gender");
  String mobile = request.getParameter("mobile");
  String event = request.getParameter("event");
  
  UserDto user = UserDto.builder()
                  .email(email)
                  .name(name)
                  .gender(gender)
                  .mobile(mobile.replace("-", ""))
                  .agree(event != null ? 1 : 0)
                  .build();
  
  
  
  
  if(userMapper.getEmailResult(email)==1||userMapper.getEmailResultInactive(email)==1)
  {
    
    try {
      
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      out.println("alert('이미 가입된 이메일입니다.')");
      out.println("history.go(-1)");
      out.println("</script>");
      out.flush();
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
 else {
  

  int naverJoinResult = userMapper.insertNaverUser(user);
  
  try {
    
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    if(naverJoinResult == 1) {
      request.getSession().setAttribute("user", userMapper.getUser(Map.of("email", email)));
      userMapper.insertAccess(email);
      out.println("alert('네이버 간편가입이 완료되었습니다.')");
    } else {
      out.println("alert('네이버 간편가입이 실패했습니다.')");
    }
    out.println("location.href='" + request.getContextPath() + "/main.do'");
    out.println("</script>");
    out.flush();
    out.close();
    
  } catch (Exception e) {
    e.printStackTrace();
  }
 } 
}

@Override
public String getNaverLoginURL(HttpServletRequest request) throws Exception {
  
  String apiURL = "https://nid.naver.com/oauth2.0/authorize";
  String response_type = "code";
  String redirect_uri = URLEncoder.encode("http://localhost:8080" + request.getContextPath() + "/user/naver/getAccessToken.do", "UTF-8");
  String state = new BigInteger(130, new SecureRandom()).toString();

  StringBuilder sb = new StringBuilder();
  sb.append(apiURL);
  sb.append("?response_type=").append(response_type);
  sb.append("&client_id=").append(client_id);
  sb.append("&redirect_uri=").append(redirect_uri);
  sb.append("&state=").append(state);
  
  return sb.toString();
	
}

@Override
public String getNaverLoginAccessToken(HttpServletRequest request) throws Exception {
  String code = request.getParameter("code");
  String state = request.getParameter("state");
  
  String apiURL = "https://nid.naver.com/oauth2.0/token";
  String grant_type = "authorization_code";  
  
  StringBuilder sb = new StringBuilder();
  sb.append(apiURL);
  sb.append("?grant_type=").append(grant_type);
  sb.append("&client_id=").append(client_id);
  sb.append("&client_secret=").append(client_secret);
  sb.append("&code=").append(code);
  sb.append("&state=").append(state);
  
  URL url = new URL(sb.toString());
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");  
  
  BufferedReader reader = null;
  int responseCode = con.getResponseCode();
  if(responseCode == 200) {
    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
  } else {
    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
  }
  
  String line = null;
  StringBuilder responseBody = new StringBuilder();
  while ((line = reader.readLine()) != null) {
    responseBody.append(line);
  }
  
  JSONObject obj = new JSONObject(responseBody.toString());
  return obj.getString("access_token");

}

@Override
public UserDto getNaverProfile(String accessToken) throws Exception {
  
  String apiURL = "https://openapi.naver.com/v1/nid/me";
  URL url = new URL(apiURL);
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");
  con.setRequestProperty("Authorization", "Bearer " + accessToken);
  
  BufferedReader reader = null;
  int responseCode = con.getResponseCode();
  if(responseCode == 200) {
    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
  } else {
    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
  }
  
  String line = null;
  StringBuilder responseBody = new StringBuilder();
  while ((line = reader.readLine()) != null) {
    responseBody.append(line);
  }
  
  JSONObject obj = new JSONObject(responseBody.toString());
  JSONObject response = obj.getJSONObject("response");
  UserDto user = UserDto.builder()
                  .email(response.getString("email"))
                  .name(response.getString("name"))
                  .gender(response.getString("gender"))
                  .mobile(response.getString("mobile"))
                  .build();
  
  return user;
}

@Override
public void naverLogin(HttpServletRequest request, HttpServletResponse response, UserDto naverProfile)throws Exception {
  String email = naverProfile.getEmail();
  UserDto user = userMapper.getUser(Map.of("email", email));
  
  if(user != null) {
    request.getSession().setAttribute("user", user);
    userMapper.insertAccess(email);
  } else {
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    out.println("alert('일치하는 회원 정보가 없습니다.')");
    out.println("location.href='" + request.getContextPath() + "/main.do'");
    out.println("</script>");
    out.flush();
    out.close();
  }
}




@Override
public void kakaoJoin(HttpServletRequest request, HttpServletResponse response) {
  String email = request.getParameter("email");
  String name = request.getParameter("name");
  String gender = request.getParameter("gender");
  String mobile = request.getParameter("mobile");
  String event = request.getParameter("event");
  


  UserDto user = UserDto.builder()
                  .email(email)
                  .name(name)
                  .gender(gender)
                  .mobile(mobile.replace("-", ""))
                  .agree(event != null ? 1 : 0)
                  .build();

  if(userMapper.getEmailResult(email)!=0||userMapper.getEmailResultInactive(email)!=0)
  {
    
    try {
      
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      out.println("alert('이미 가입된 이메일입니다.')");
      out.println("history.go(-1)");
      out.println("</script>");
      out.flush();
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  else {
    
  
  int kakaoJoinResult = userMapper.insertKakaoUser(user);
  
  try {
    
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    if(kakaoJoinResult == 1) {
      request.getSession().setAttribute("user", userMapper.getUser(Map.of("email", email)));
      userMapper.insertAccess(email);
      out.println("alert('카카오 간편가입이 완료되었습니다.')");
    } else {
      out.println("alert('카카오 간편가입이 실패했습니다.')");
    }
    out.println("location.href='" + request.getContextPath() + "/main.do'");
    out.println("</script>");
    out.flush();
    out.close();
    
  } catch (Exception e) {
    e.printStackTrace();
  }
  }
}


@Override
public String getKakaoLoginURL(HttpServletRequest request) throws Exception {



  String apiURL = "https://kauth.kakao.com/oauth/authorize";
  String response_type = "code";
  String redirect_uri = URLEncoder.encode("http://localhost:8080" + request.getContextPath() + "/user/kakao/getAccessToken.do", "UTF-8");
  String state = new BigInteger(130, new SecureRandom()).toString();

  StringBuilder sb = new StringBuilder();
  sb.append(apiURL);
  sb.append("?response_type=").append(response_type);
  sb.append("&client_id=").append(kakao_api_key);
  sb.append("&redirect_uri=").append(redirect_uri);
  sb.append("&state=").append(state);
  
 
  return sb.toString();
  

}

@Override
public String getKakaoLoginAccessToken(HttpServletRequest request) throws Exception {

  
  String code = request.getParameter("code");
  String state = request.getParameter("state");
  
  String apiURL = "https://kauth.kakao.com/oauth/token";
  String grant_type = "authorization_code";  
  
  StringBuilder sb = new StringBuilder();
  sb.append(apiURL);
  sb.append("?grant_type=").append(grant_type);
  sb.append("&client_id=").append(kakao_api_key);
  sb.append("&client_secret=").append(kakao_secret_key);
  sb.append("&code=").append(code);
  sb.append("&state=").append(state);
  
  URL url = new URL(sb.toString());
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");  
  
  BufferedReader reader = null;
  int responseCode = con.getResponseCode();
  if(responseCode == 200) {
    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
  } else {
    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
  }
  
  String line = null;
  StringBuilder responseBody = new StringBuilder();
  while ((line = reader.readLine()) != null) {
    responseBody.append(line);
  }
  
  JSONObject obj = new JSONObject(responseBody.toString());

  return obj.getString("access_token");
}
@Override
public UserDto getKakaoProfile(String accessToken) throws Exception {
  
  String apiURL = " https://kapi.kakao.com/v2/user/me";
  URL url = new URL(apiURL);
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");
  con.setRequestProperty("Authorization", "Bearer " + accessToken);

  BufferedReader reader = null;
  int responseCode = con.getResponseCode();
  if(responseCode == 200) {
    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
  } else {
    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
  }
  
  String line = null;
  StringBuilder responseBody = new StringBuilder();
  while ((line = reader.readLine()) != null) {
    responseBody.append(line);
  }
  
  JSONObject obj = new JSONObject(responseBody.toString());


  JSONObject kakao_account = obj.getJSONObject("kakao_account");
  JSONObject profile=kakao_account.getJSONObject("profile");
  
  UserDto user = UserDto.builder()
                  .email(kakao_account.getString("email"))
                  .name(profile.getString("nickname"))
                  .build();
  
  return user;
}



@Override
public void kakaoLogin(HttpServletRequest request, HttpServletResponse response, UserDto kakaoProfile)throws Exception {
  
  
  String email = kakaoProfile.getEmail();
  UserDto user = userMapper.getUser(Map.of("email", email));
  
  if(user != null) {
    request.getSession().setAttribute("user", user);
    userMapper.insertAccess(email);
  } else {
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    out.println("alert('일치하는 회원 정보가 없습니다.')");
    out.println("location.href='" + request.getContextPath() + "/main.do'");
    out.println("</script>");
    out.flush();
    out.close();
  }
}



@Override
public void inactiveUserBatch() {
	
  userMapper.insertInactiveUser();
  userMapper.deleteUserForInactive();
}

@Override
public void active(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
	
	
  InactiveUserDto inactiveUser = (InactiveUserDto)session.getAttribute("inactiveUser");
  String email = inactiveUser.getEmail();
  
  int insertActiveUserResult = userMapper.insertActiveUser(email);
  int deleteInactiveUserResult = userMapper.deleteInactiveUser(email);
  
  try {
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script>");
    if(insertActiveUserResult == 1 && deleteInactiveUserResult == 1) {
      out.println("alert('휴면계정이 복구되었습니다. 계정 활성화를 위해서 곧바로 로그인 해 주세요.')");
      out.println("location.href='" + request.getContextPath() + "/main.do'"); 
    } else {
      out.println("alert('휴면계정이 복구가 실패했습니다. 다시 시도하세요.')");
      out.println("history.back()");
    }
    out.println("</script>");
    out.flush();
    out.close();
  } catch (Exception e) {
    e.printStackTrace();
  }
	
	
}





@Override
public Map<String, Object> editProfile(MultipartHttpServletRequest multipartRequest) throws Exception {
  
	
	int editResult=1;
	int attachCount;
	
	 List<MultipartFile> files = multipartRequest.getFiles("files");
	 int userNo=Integer.parseInt(multipartRequest.getParameter("userNo"));
	 
	 
	 
	    if(files.get(0).getSize() == 0) {
	        attachCount = 1;
	      } else {
	        attachCount = 0;
	      }
	    
	    MultipartFile multipartFile = files.get(0); 
	    
	    if(multipartFile != null && !multipartFile.isEmpty()) {
	    	
	        String path ="/user/myProfile"; 
	        File dir = new File(path);
	        if(!dir.exists()) {
	          dir.mkdirs();
	        }
	        
        String originalFilename = multipartFile.getOriginalFilename(); 
        String filesystemName = myFileUtils.getFilesystemName(originalFilename);
        File file = new File(dir, filesystemName);   
   
        multipartFile.transferTo(file);
	    
	
	    

        ProfileDto profile=ProfileDto.builder()
        							 .path(path)
        							 .originalFilename(originalFilename)
        							 .filesystemName(filesystemName)
        							 .userDto(UserDto.builder()
        									 		 .userNo(userNo)
        									 		 .build())
        							 .build();
        
        profileMapper.deleteOld(profile);
        editResult=profileMapper.insertProfile(profile);  

	    }

  return Map.of("editResult",editResult);
}


@Override
	public Map<String, Object> getProfileImage(HttpServletRequest request) {
		
	int userNo=Integer.parseInt(request.getParameter("userNo"));
	
	
	ProfileDto profile=ProfileDto.builder()
								 .userDto(UserDto.builder()
											.userNo(userNo)
											.build())
								 .build();
								 
	
	return Map.of("profile", profileMapper.getProfileImage(profile));
	}	


@Override
public void removeProfileImage(HttpServletRequest request) {

  int userNo=Integer.parseInt(request.getParameter("userNo"));

  ProfileDto profile=ProfileDto.builder()
      .path("null")
      .originalFilename("null")
      .filesystemName("null")
      .userDto(UserDto.builder()
              .userNo(userNo)
              .build())
      .build();
  
  profileMapper.deleteOld(profile);
  
  
  profileMapper.insertProfile(profile);
}



}








