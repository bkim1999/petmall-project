<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="dt" value="<%=System.currentTimeMillis()%>" />

<jsp:include page="../layout/header.jsp">
  <jsp:param value="아이디 찾기" name="title"/>
</jsp:include>

<div>
<h1>아이디 찾기</h1>


<form  id="frm_find_id">
  <div>
    <label for="name" >이름</label>
    <input type="text"  id="name" name="name">
  </div>
  
  <div>
       <label for="mobile" >휴대폰 번호</label>
    <input type="text"  id="mobile" name="mobile">
  </div>
  
  <div>
    <button type="button" id="btn_find_id">아이디 찾기</button>
   
  </div>
  
  
</form>



<!-- 스크립트 영역 -->

<script>

/* **************************컨텍스트 패스**************************** */
const getContextPath = () => {
  let begin = location.href.indexOf(location.host) + location.host.length;
  let end = location.href.indexOf('/', begin + 1);
  return location.href.substring(begin, end);
}

/****************************아이디찾기**********************************/
const fnFindId=()=>{
	
	$('#btn_find_id').click(()=>{
		
		$.ajax({
			type:'post',
			url:getContextPath() +'/user/find_id.do',
			data:$('#frm_find_id').serialize(),
			
			dataType:'json',
			success:(resData)=>{
				
				if(resData.email==0)
				{
					alert('일치하는 회원정보가 없습니다.');
					return;
				}
				
				alert('아이디는'+resData.email+'입니다.');
				location.href=getContextPath() +'/user/login.form';
			}
		})
	})
}


fnFindId();

</script>


</div>



<%@ include file="../layout/footer.jsp" %>


    

