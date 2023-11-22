<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="dt" value="<%=System.currentTimeMillis()%>" />

<jsp:include page="../layout/header.jsp">
  <jsp:param value="로그인" name="title"/>
</jsp:include>


<style>

/*중앙정렬*/
.btn_naver{margin-top:40px;}
.login_wrap{margin:0 auto; width:290px;}


/*버튼간 간격*/
.btn_margin{margin-bottom:10px;}

/*네이버 가입버튼*/
.btn_naver{margin-top:40px;}
.btn_naver img{width:290px; height:70px;}
.join_opt_wrap{margin:0 auto;}

/*로그인 폼 */
.form-control{margin-bottom:10px;}


/*로그인 버튼*/
#btn_login{width:290px; height:60px;}

/*비번/아이디 찾기*/
.bar::before{
 content: "|";
 color:gray;
}
.span_center{text-align:center;}
</style>



<div class="logo"><a href="${contextPath}/main.do"><img alt="로고" src=""></a></div>


<hr>
<div class="login_wrap">













<!-- 카카오 로그인 페이지 이동  
<div><button>카카오로 시작하기</button></div>
-->

<!-- 네이버 로그인 페이지 이동  -->
<div class="btn_naver btn_margin"><a href="${naverLoginURL}"><img src="${contextPath}/resources/assets/image/btnG_완성형.png" width="200px" ></a></div>


<!-- 로그인 폼  (post)-->
<form method="post" action="${contextPath}/user/login.do" id="frm_login" >



<div><input type="text"   id="email"  name="email"  placeholder="ID(Email)" class="form-control"></div>
<div><input type="password"  id="pw" name="pw"  placeholder="PASSWORD" class="form-control"></div>


  <div>
      <input type="hidden" name="referer" value="${referer}">
      <div><button id="btn_login" type="submit"  class="btn btn-lg btn-primary">로그인</button></div>
      
  </div>

<div class="span_center">
<span ><a href="${contextPath}/user/find_id.form">아이디찾기</a></span>
<span class="bar"><a href="${contextPath}/user/change_pw.form">비밀번호분실</a></span>
<span class="bar"><a href="${contextPath}/user/join_option.form">회원가입</a></span>
</div>



</form>


</div>



<%@ include file="../layout/footer.jsp" %>


    

