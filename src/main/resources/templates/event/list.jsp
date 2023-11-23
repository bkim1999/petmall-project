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

<style>

  .Middle {
   border-radius: 10px;
   background-color: white;
   margin :20px;
  }
  
  .top{
    padding: 100px;
    background-color: #23919770;
  }
  
  .bottom{
    margin : 20px;
    background-color: white;
    border-radius: 10px;
    text-align: center;
  }
  
  .img1 {
    width: 100%;
    height: 100%;
    border-radius: 10px;
    text-align: center;
  }
  .img2 {
    width: 100%;
    height: 100%;
    border-radius: 10px;
    text-align: center;
  }
  .second_top {
    background-color: white;
    border-radius: 10px;
    padding : 20px;
  }
  .btn_by {
    display:flex;
    justify-content: space-between;
    margin : 0px 130px;
  }
  
  .bottom a{
   padding : 20px; 
  }
  .event_font{
    text-align: center;
  }
</style>


  <div>
   <div class="event_font">페스룸 회원가입하고 첫 구매 100원 회원가입만 하면 프리미엄 휴먼그레이드 간식이 100원!</div>
   <div class="btn_by">
    <img class="cat1" src="https://pethroom.com/web/upload/NNEditor/page/event-alex.png" width=350px height=170px>
    <a href="${contextPath}/product/list.do">
     <input type="button" value="구매하로가기">
    </a>
    <img class="dog1" src="https://pethroom.com/web/upload/NNEditor/page/event-boss.png" width=350px height=170px>
   </div>
  </div>
  <div class="top">
   <div class ="second_top">
    <c:forEach items="${eventList}" var="event" varStatus="vs">
     <div class="Middle">
       <c:if test="${sessionScope.user.userNo != null}">
        <a href="${contextPath}/event/increase.do?eventNo=${event.eventNo}"><img class="img1" src="${contextPath}${event.eventThumnailUrl}" width="1250px" height="300px"></a>
       </c:if>
       <c:if test="${sessionScope.user.userNo == null}">
        <a href="${contextPath}/event/detail.do?eventNo=${event.eventNo}"><img class="img2" src="${contextPath}${event.eventThumnailUrl}" width="1250px" height="300px"></a>
       </c:if>
     </div>
    </c:forEach>
    <div class="bottom">${paging}</div>
    </div>
  </div>



<%@ include file="../layout/footer.jsp" %>