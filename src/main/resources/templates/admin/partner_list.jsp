<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="dt" value="<%=System.currentTimeMillis()%>" />

<jsp:include page="../layout/header.jsp">
  <jsp:param value="이벤트게시판" name="title"/>
</jsp:include>

  <div>파트너십 페이지에 오신것을 환영합니다.</div>
  
  <div>전체 문의글수 : ${qnaTotalCount}</div>
  <div>답변 완료된 문의글 수 : ${qnaAnswerCount}</div>
  <div>미답변된 문의글 수 : ${qnaNonAnswerCount}</div>
  
  <table border=1>
    <thead>
      <tr>
       <td>문의글 번호</td>
       <td>제목</td>
       <td>문의일</td>
       <td>답변여부</td>
       <td>답변하로가기</td>
      </tr>
    </thead>
    <tbody>
     <c:forEach items="${inquiryList}" var ="inquiry" varStatus="vs">
      <tr>
       <td>${inquiry.inquiryNo}</td>
       <td>${inquiry.title}</td>
       <td>${inquiry.createdAt}</td>
       <c:if test="${inquiry.checkFlag == 1}">
       <td>답변완료</td>
       </c:if>
       <c:if test="${inquiry.checkFlag == 0}">
       <td>미답변</td>
       </c:if>
       <td>
        <input type="hidden" value="${inquiry.groupNo}">
        <c:if test="${inquiry.checkFlag == 1}">
        <input type="button" id="btn_answer" value="추가답변하기">
        </c:if>
        <c:if test="${inquiry.checkFlag == 0}">
        <input type="button" id="btn_answer" value="답변하기">
        </c:if>
        <input type="hidden" value="${inquiry.inquiryNo}">
       </td>
      </tr>
     </c:forEach>
    </tbody>
  </table>
  
   <div>
    <a href="${contextPath}/admin/admin.go"><input type="button" value="관리자페이지돌아가기"></a>
  </div>
  
 <script>
  function fnAnswer() {
	$(document).on('click', '#btn_answer', function(ev){
	  location.href = '${contextPath}/user/qnadetail.do?qnaNo='+$(this).next().val()+'&groupNo='+$(this).prev().val();
	})
  }
  
  fnAnswer();
 </script>
  
  
    


<%@ include file="../layout/footer.jsp" %>