package com.gdu.petmall.interceptor;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gdu.petmall.dao.AutoLoginMapper;
import com.gdu.petmall.dao.UserMapper;
import com.gdu.petmall.dto.AutoLoginDto;
import com.gdu.petmall.dto.UserDto;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class AutoLoginInterceptor implements HandlerInterceptor {

	private final AutoLoginMapper autoLoginMapper;
	private final UserMapper userMapper;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		/*1. 세션에 사용자가 있는지 확인*/
		
		HttpSession session=request.getSession();
		UserDto user = (UserDto) session.getAttribute("user");
		String sessionId=session.getId();
		Cookie cookies[] = request.getCookies();
		String userCookie="";
		
		if(cookies!=null) {
			for(Cookie c : cookies) {
	            String name = c.getName(); // 쿠키 이름 가져오기
	            String value = c.getValue(); // 쿠키 값 가져오기
	            
	            if (name.equals("userCookie")) {
	                userCookie=value;
	            
	            }

			}//for
			
			
		}//if
		
		if(user!=null)
		{
			//로그인 상태
		}
		else {//로그아웃상태
			
			
			//세션에 쿠키값이 존재한다면
			if(userCookie!=null)
			{
			/*db 조회*/
             AutoLoginDto autoLoginDto= autoLoginMapper.getAutoLoginTokenInfo(userCookie);
             		
             
              //db조회 결과값이 null이 아니면 로그인 처리
             	if(autoLoginDto != null)
             	{
             		
             		
             		String email=autoLoginDto.getEmail();
             		String pw=autoLoginDto.getPw();
             		int userNo=autoLoginDto.getUserNo();
             		
             		Map<String, Object>map=Map.of("email",email,"pw",pw,"userNo",userNo);
             		UserDto autoUser= userMapper.getUser(map);
             		
             		
             	    request.getSession().setAttribute("user", autoUser);
             	    userMapper.insertAccess(email);
             	    response.sendRedirect(request.getContextPath() + "/main.do");
             		
             		
             	}
             	else {
             		//자동로그인 안돼서 메인으로 리다이렉트
				}

			}
      
            
            

		}
		
		return true;
	}


}
