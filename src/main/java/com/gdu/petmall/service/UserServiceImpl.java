package com.gdu.petmall.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.gdu.petmall.dao.ProfileMapper;
import com.gdu.petmall.dao.UserMapper;
import com.gdu.petmall.dto.InactiveUserDto;
import com.gdu.petmall.dto.ProfileDto;
import com.gdu.petmall.dto.UserDto;
import com.gdu.petmall.util.MyFileUtils;
import com.gdu.petmall.util.MyJavaMailUtils;
import com.gdu.petmall.util.MySecurityUtils;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;


@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserMapper userMapper;
	private final ProfileMapper profileMapper;
	private final MySecurityUtils mySecurityUtils;
	private final MyJavaMailUtils myJavaMailUtils;
	private final MyFileUtils myFileUtils;

	
	/*네이버 api 클라이언트정보*/
  private final String client_id = "OsFLRfpdkM0BZr7yfZrr";
  private final String client_secret = "eiyecu8MwC";
  
  /*카카오 api 클라이언트정보*/
  private final String kakao_api_key="c3db793b2723f0cd2a96e05470dbdc6f";
  private final String kakao_secret_key="i1Bd2PWwfhLAgkJ93YkXdYaf2v5UZc60";
	
/*로그인*/
@Override
public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
	 
  String email = mySecurityUtils.preventXSS(request.getParameter("email"));
  String pw = mySecurityUtils.getSHA256(request.getParameter("pw")); 
  
  Map<String, Object> map = Map.of("email", email
                                 , "pw", pw);
	
  
  HttpSession session = request.getSession();
  
  
  //휴면 회원인지 확인
  InactiveUserDto inactiveUser = userMapper.getInactiveUser(map);
  if(inactiveUser != null) {
    session.setAttribute("inactiveUser", inactiveUser);
    response.sendRedirect(request.getContextPath() + "/user/active.form"); // 계정 활성페이지로 보내
  }
  
  
  //로그인 하기
  UserDto user = userMapper.getUser(map);
  
  
  
  if(user != null) {				// 세션에 유저가 없으면
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
	




// 회원정보 가져와
@Override
public UserDto getUser(String email) {
  return userMapper.getUser(Map.of("email", email));
}


/*로그아웃*/
@Override
public void logout(HttpServletRequest request, HttpServletResponse response) {

HttpSession session=request.getSession();
session.invalidate();// 세션 초기화

try {
	response.sendRedirect(request.getContextPath()+"/main.do");
	
}catch (Exception e) {
e.printStackTrace();
}

}





/*이메일 체크*/
@Transactional(readOnly=true)
@Override
public ResponseEntity<Map<String, Object>> checkEmail(String email) {
  
  Map<String, Object> map = Map.of("email", email);
  
  boolean enableEmail = userMapper.getUser(map) == null
                     && userMapper.getLeaveUser(map) == null
                     && userMapper.getInactiveUser(map) == null;
  
  return new ResponseEntity<>(Map.of("enableEmail", enableEmail), HttpStatus.OK);
  
}


/*인증코드 전송*/
@Override
public ResponseEntity<Map<String, Object>> sendCode(String email) {
  
  // RandomString 생성(6자리, 문자 사용, 숫자 사용)
  String code = mySecurityUtils.getRandomString(8, true, true);
  
  // 메일 전송
  myJavaMailUtils.sendJavaMail(email
                             , "petmall 인증 코드"
                             , "<div>인증코드는 <strong>" + code + "</strong>입니다.</div>");
  
  return new ResponseEntity<>(Map.of("code", code), HttpStatus.OK);
  
}




/*회원가입*/
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
  
  

  
  
  
  
 // 이벤트 수신 동의 체크 안돼있으면 null 전달됨. null 처리 해야함
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
  
  // 이메일이 db에 존재하는지 확인하기.
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



/*회원탈퇴*/
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
	

/*회원정보수정*/
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
	  
	  
	  
	 // 이벤트 수신 동의 체크 안돼있으면 null 전달됨. null 처리 해야함
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



/*포인트*/
@Override
public void getPoint(HttpServletRequest request, Model model) {

	int userNo=Integer.parseInt(request.getParameter("userNo"));
	Map<String,Object> map=Map.of("userNo",userNo);
	
	int  point=userMapper.getPoint(map);
	
	model.addAttribute("point",point);
	model.addAttribute("userNo",userNo);
	
}


/*아이디 찾기*/
@Override
public ResponseEntity<Map<String, Object>> findId(HttpServletRequest request) {
	
	String name= mySecurityUtils.preventXSS(request.getParameter("name"));
	String mobile= mySecurityUtils.preventXSS(request.getParameter("mobile"));
	
	String email= userMapper.getEmail(Map.of("name",name,"mobile",mobile));
	
	// 아이디가 검색되었을때
	if(email!=null)
	{
		return new ResponseEntity<>(Map.of("email", email), HttpStatus.OK);
	}
	else {
		return new ResponseEntity<>(Map.of("email", 0), HttpStatus.OK);
	}
	
	
}


/*일치하는 이메일 조회(비번 변경용)*/
@Override
public ResponseEntity<Map<String, Object>> changePw(HttpServletRequest request) {
	
	
	String email=mySecurityUtils.preventXSS(request.getParameter("email"));	
	
	
	
	// 일치하는 이메일이 있으면 비번 변경폼 보여주고 
	//email을 modfyPw로 전달해야함 
	if(userMapper.getEmailforPw(Map.of("email",email))==1) {
		
		return new ResponseEntity<>(Map.of("result", 1), HttpStatus.OK);
		
	}else {
		return new ResponseEntity<>(Map.of("result", 0), HttpStatus.OK);
	}	

}

/*비번 변경 적용*/
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

/* **************************네이버 api 관련 *********************** */
/*네이버 간편 가입*/
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
  
  
  
  
  // 이메일이 db에 존재하는지 확인하기.
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


/*네이버 로그인 연동 url  생성*/
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

/*네이버 로그인 접근  토큰 발급 요청*/
@Override
public String getNaverLoginAccessToken(HttpServletRequest request) throws Exception {
  String code = request.getParameter("code");
  String state = request.getParameter("state");
  
  String apiURL = "https://nid.naver.com/oauth2.0/token";
  String grant_type = "authorization_code";  // access_token 발급 받을 때 사용하는 값(갱신이나 삭제시에는 다른 값을 사용함)
  
  StringBuilder sb = new StringBuilder();
  sb.append(apiURL);
  sb.append("?grant_type=").append(grant_type);
  sb.append("&client_id=").append(client_id);
  sb.append("&client_secret=").append(client_secret);
  sb.append("&code=").append(code);
  sb.append("&state=").append(state);
  
  // 요청
  URL url = new URL(sb.toString());
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");  // 반드시 대문자로 작성
  
  // 응답
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

/*네이버 로그인 후속작업*/
@Override
public UserDto getNaverProfile(String accessToken) throws Exception {
  
  // 요청
  String apiURL = "https://openapi.naver.com/v1/nid/me";
  URL url = new URL(apiURL);
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");
  con.setRequestProperty("Authorization", "Bearer " + accessToken);
  
  // 응답
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
  
  // 응답 결과(프로필을 JSON으로 응답) -> UserDto 객체
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

/*네이버 로그인 */
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
/* ***************************************************************** */




/* ***************************카카오 api 관련****************************** */
/*카카오 간편 가입*/
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

  // 이메일이 db에 존재하는지 확인하기.
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


/*카카오 로그인 연동 url  생성*/
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

/*카카오 로그인 접근  토큰 발급 요청*/
@Override
public String getKakaoLoginAccessToken(HttpServletRequest request) throws Exception {

  
  String code = request.getParameter("code");
  String state = request.getParameter("state");
  
  String apiURL = "https://kauth.kakao.com/oauth/token";
  String grant_type = "authorization_code";  // access_token 발급 받을 때 사용하는 값(갱신이나 삭제시에는 다른 값을 사용함)
  
  StringBuilder sb = new StringBuilder();
  sb.append(apiURL);
  sb.append("?grant_type=").append(grant_type);
  sb.append("&client_id=").append(kakao_api_key);
  sb.append("&client_secret=").append(kakao_secret_key);
  sb.append("&code=").append(code);
  sb.append("&state=").append(state);
  
  // 요청
  URL url = new URL(sb.toString());
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");  // 반드시 대문자로 작성
  
  // 응답
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
/*카카오 로그인 후속작업*/
@Override
public UserDto getKakaoProfile(String accessToken) throws Exception {
  
  // 요청
  String apiURL = " https://kapi.kakao.com/v2/user/me";
  URL url = new URL(apiURL);
  HttpURLConnection con = (HttpURLConnection)url.openConnection();
  con.setRequestMethod("GET");
  con.setRequestProperty("Authorization", "Bearer " + accessToken);
  
  // 응답
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
  
  // 응답 결과(프로필을 JSON으로 응답) -> UserDto 객체
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



/* ******************************************************************* ** */ 


/*회원 휴면 처리*/
@Override
public void inactiveUserBatch() {
	
  userMapper.insertInactiveUser();
  userMapper.deleteUserForInactive();
}

/*휴면 복원*/
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





/* ************************************** 프로필 이미지 ****************************/

/*프로필 이미지 첨부*/
@Override
public Map<String, Object> editProfile(MultipartHttpServletRequest multipartRequest) throws Exception {
  
	
	int editResult=1;
	int attachCount;
	
	// ajax로 전달한 formData 정보
	 List<MultipartFile> files = multipartRequest.getFiles("files");
	 int userNo=Integer.parseInt(multipartRequest.getParameter("userNo"));
	 
	 
	 
	//업로드된 파일이 있는지 없는지 확인
	    if(files.get(0).getSize() == 0) {
	        attachCount = 1;
	      } else {
	        attachCount = 0;
	      }
	    
	    MultipartFile multipartFile = files.get(0); 
	    
	//파일이 존재하는지
	    if(multipartFile != null && !multipartFile.isEmpty()) {
	    	
	        String path ="/user/myProfile"; // 파일 저장할 경로
	        File dir = new File(path);
	        if(!dir.exists()) {
	          dir.mkdirs();
	        }//if2
	        
	//  파일 생성
        String originalFilename = multipartFile.getOriginalFilename(); //원본이름
        String filesystemName = myFileUtils.getFilesystemName(originalFilename);//저장이름
        File file = new File(dir, filesystemName);   // 파일생성(저장경로,파일명)
   
    // 서버에 생성된 파일 저장
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

	    }//if1

  return Map.of("editResult",editResult);
}


/*프로필 이미지 가져오기*/
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


/*프로필 이미지 삭제하기*/
@Override
public void removeProfileImage(HttpServletRequest request) {

  int userNo=Integer.parseInt(request.getParameter("userNo"));
  
  // 삭제시 대체할 정보
  ProfileDto profile=ProfileDto.builder()
      .path("null")
      .originalFilename("null")
      .filesystemName("null")
      .userDto(UserDto.builder()
              .userNo(userNo)
              .build())
      .build();
  
  // 삭제하기
  profileMapper.deleteOld(profile);
  
  
  //삽입하기
  profileMapper.insertProfile(profile);
}



}








