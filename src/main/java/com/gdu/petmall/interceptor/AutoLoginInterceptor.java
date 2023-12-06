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
		
		
		HttpSession session=request.getSession();
		UserDto user = (UserDto) session.getAttribute("user");
		Cookie cookies[] = request.getCookies();
		String userCookie="";
		
		if(cookies!=null) {
			for(Cookie c : cookies) {
	            String name = c.getName(); 
	            String value = c.getValue();
	            
	            if (name.equals("userCookie")) {
	                userCookie=value;
	            
	            }

			}
			
			
		}
		
		if(user!=null)
		{
			
		}
		else {
			
			
			
			if(userCookie!=null)
			{
			
             AutoLoginDto autoLoginDto= autoLoginMapper.getAutoLoginTokenInfo(userCookie);
             		
             
              
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

			}
      
            
            

		}
		
		return true;
	}


}
