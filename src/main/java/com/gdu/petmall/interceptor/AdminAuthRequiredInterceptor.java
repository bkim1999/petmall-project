package com.gdu.petmall.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gdu.petmall.dto.UserDto;

/**
 * AdminAuthRequiredInterceptor
 * 관리자 권한이 필요한 기능을 요청할 때 관리자 권한 여부를 점검하는 인터셉터
 */

@Component
public class AdminAuthRequiredInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    
    HttpSession session = request.getSession();
    UserDto user = (session == null) ? null : (UserDto) session.getAttribute("user");
    if(session != null && user != null && user.getAdminAuthorState() != 1) {
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      out.println("alert('관리자 권한이 필요합니다.')");
      out.println("history.back()");
      out.println("</script>");
      out.flush();
      out.close();
      
      return false;  
      
    }
    
    return true;     
    
  }
  
}