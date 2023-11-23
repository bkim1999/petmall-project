<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="dt" value="<%=System.currentTimeMillis()%>" />

<jsp:include page="../layout/header.jsp">
  <jsp:param value="이벤트상세게시판" name="title"/>
</jsp:include>
 
 <style>
  div {
    text-align: center;
  }
 </style>
  

  <h1>EVENT</h1>
  <hr>
  <div>${event.title}</div>
  <div>${event.startAt} ~ ${event.endAt}</div>
  <hr>
    <div>조회수 ${event.hit}</div>
  <hr>
  <div>${event.contents}</div>
    <div>${event.eventImageDto.path}</div>
    <c:if test="${event.eventImageDto.path != null}">
      <img src="${event.eventImageDto.path}" width = "500px" height = "300px">   
    </c:if>
    <c:if test="${event.eventImageDto.path == null}">
     <div></div>
    </c:if>
   
   <div>
    <input type="button" value="다른이벤트 보로가기" id="other_event">
   </div>
   
   
   <script>
   function fnOtherEvent(){
   	 $(document).on('click', '#other_event', function(ev){
	   location.href = '${contextPath}/event/list.do';
	})
   }
   fnOtherEvent();
   </script>

<%@ include file="../layout/footer.jsp" %>