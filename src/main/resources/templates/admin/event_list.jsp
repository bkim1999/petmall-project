<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="dt" value="<%=System.currentTimeMillis()%>" />

<jsp:include page="../layout/header.jsp">
  <jsp:param value="이벤트상세보기" name="title"/>
</jsp:include>


<style>
  .table_font {
    font-size: 13px;
    text-align: center;
    line-height: 20px;
  }
  .tr_font {
   line-height: 30px;
  }
  .td_font {
    border-right: 1px solid gray;
    padding-top: 20px;
  }
</style>

  <div>
    <button type="button" class="btn_write">이벤트 추가하기</button>
    <a href="${contextPath}/admin/admin.go"><input type="button" value="관리자페이지돌아가기"></a>  
  </div>
    
  <table border="1" class="table table-hover table_font">  
    <thead>
      <tr>
        <td>이벤트 번호</td>
        <td>이벤트 이름</td>
        <td>이벤트 사진</td>       
        <td>이벤트 시작일</td>       
        <td>이벤트 종료일</td>       
        <td>이벤트 할인율</td>       
        <td>이벤트 할인가</td>       
        <td>이벤트 종료여부</td>       
        <td>이벤트 상세보기</td>
        <td>이벤트 시작하기</td>
        <td>이벤트 종료하기</td>       
      </tr>
    </thead>
    <tbody id="event_list"></tbody>
  </table>
  
  <div>
    <a href="${contextPath}/admin/admin.go"><input type="button" value="관리자페이지돌아가기"></a>
  </div>
  
   <script>
   
    function fnReEventList() {
     $.ajax({
    	// 요청
    	type: 'get',
    	url : '${contextPath}/admin/event_list.do',
    	// 응답
    	dataType:'json',
    	success: (resData) => {
         $('#event_list').empty();
    	 if(resData.eventList === null){
    		alert('목록 불러오기 실패');
    		return;
    	 }
    	 $.each(resData.eventList, (i, event) => {
    	   let str ='<tr class="table-light tr_font">'
    	   str +='<td class="td_font">'+event.eventNo+'</td>';
    	   str +='<td class="td_font">'+event.title+'</td>';
    	   str +='<td class="td_font"><img src="'+'${contextPath}'+event.eventThumnailUrl+'"width="100px" height="100px"></td>'
    	   str +='<td class="td_font">'+event.startAt+'</td>';
    	   str +='<td class="td_font">'+event.endAt+'</td>';
    	   str +='<td class="td_font">';
    	   str +='<input type="text" value="'+event.discountPercent+'" readonly>';
    	   str +='<div>';
    	   str +='<form class="frm_changePercent">';
    	   str +='<input type="text" name="discountPercent" placeholder="변경될할인율입력">';
    	   str +='<input class="change_Percent" type="submit" value="할인율 변경하기">';
    	   str +='<input type="hidden" name="eventNo" value="'+event.eventNo+'">';
    	   str +='</form>';
    	   str +='</div>';
    	   str +='</td>';
    	   str +='<td class="td_font">';
    	   str +='<input type="text" value="'+event.discountPrice+'" readonly>';
    	   str +='<div>';
    	   str +='<form class="frm_changePrice">';
    	   str +='<input type="text" name="discountPrice" placeholder="변경될할인가입력">';
    	   str +='<input class="change_Price" type="submit" value="할인가 변경하기">';
    	   str +='<input type="hidden" name="eventNo" value="'+event.eventNo+'">';
    	   str +='</form>';
    	   str +='</div>';
    	   str +='</td>';
		   if(event.state === 1){
	    	str +='<td class="td_font">이벤트 진행중</td>'; 			   
		   }
    	   if(event.state === 0){
    		str +='<td class="td_font">이벤트 종료</td>';   
    	   }
    	   str +='<td class="td_font">';
    	   str +='<input type="button" class="btn_event" value="상세보기">';
    	   str +='<input type="hidden" value="'+event.eventNo+'">';
    	   str +='</td>';
    	   str +='<td class="td_font">';
    	   str +='<input type="hidden" value="'+event.state+'">';
    	   str +='<input type="button" class="btn_event_start" value="이벤트시작하기">';
    	   str +='<input type="hidden" value="'+event.eventNo+'">';
    	   str +='</td>';
    	   str +='<td class="td_font">';
    	   str +='<input type="hidden" value="'+event.state+'">';
    	   str +='<input type="button" class="btn_event_end" value="이벤트종료시키기">';
    	   str +='<input type="hidden" value="'+event.eventNo+'">';
    	   str +='</td>';
    	   str +='</tr>';
    	   $('#event_list').append(str);
    	 });
    	}
     })
    }
   
   
    
    
    function fnDetailEvent() {
      $(document).on('click', '.btn_event', function(ev){
        location.href = '${contextPath}/event/detail.do?eventNo='+$(this).next().val();
      })
    }
    
    
    // 시작종료 막아야함.
    function fnEventEnd() {
      $(document).on('click', '.btn_event_end',function(ev){
        if($(this).prev().val() === '0'){
            alert('이미 종료된 이벤트입니다.');
            return;
          }
    	if(!confirm('해당 이벤트를 종료시키겠습니까?')){
    	  return;
    	}
      $.ajax({
    	// 요청
    	type : 'get',
    	url: '${contextPath}/event/eventEnd.do?eventNo='+$(this).next().val(),
    	// 응답
    	dataType: 'json',
    	success: (resData) => {
    	  if(resData.endResult === 1) {
    		alert('이벤트가 종료되었습니다.');
    		fnReEventList();
    	  } else {
    		alert('이벤트 종료가 실패했습니다');
    		return;
    	  }
    	}
      })	
    	
      })
    }
    
    function fnEventStart() {
        $(document).on('click', '.btn_event_start',function(ev){
        if($(this).prev().val() === '1'){
          alert('이미 진행중인 이벤트입니다.');
          return;
        }
      	if(!confirm('해당 이벤트를 시작하겠습니까?')){
      	  return;
      	}
        $.ajax({
      	// 요청
      	type : 'get',
      	url: '${contextPath}/event/eventStart.do?eventNo='+$(this).next().val(),
      	// 응답
      	dataType: 'json',
      	success: (resData) => {
      	  if(resData.startResult === 1) {
      		alert('이벤트가 시작되었습니다.');
      		fnReEventList();
      	  } else {
      		alert('이벤트 시작이 실패했습니다');
      		return;
      	  }
      	}
        })	
      	
        })
      }
    
    
     function fnEventWrite() {
       $('.btn_write').on('click',function(ev){
    	 location.href = '${contextPath}/event/write.do';
       })
     } 
     
     function fnChangePercent() {
       $(document).on('click','.change_Percent',function(ev){
    	let maxPercent = 100;
    	let minPercent = 0;
        if($(this).prev().val() > maxPercent){
            alert('할인율의 최대는 100입니다');
            return;
        }
        if($(this).prev().val() < minPercent){
            alert('할인율의 최소는 0입니다');
            return;	
        }
       	if(!confirm('해당 할인율을 변경시키겠습니까?')){
      	  return;
         }
       	$.ajax({
       	  //요청
       	  type:'get',
          url: '${contextPath}/event/changePercent.do',
          data: $(this).parent().serialize(),
          // 응답
      	  dataType: 'json',
      	  success : (resData) => {
      		if(resData.PercentResult === 1){
      		  alert('할인율이 변경되었습니다.');
      		  fnReEventList();	
      		} else {
          	  alert('할인율이 변경되지않았습니다.');
          	  return;	
      		}
      	  }
       	})
       })
     }
     
     function fnChangePrice() {
         $(document).on('click','.change_Price',function(ev){
         	if(!confirm('해당 할인율을 변경시키겠습니까?')){
        	  return;
           }
         	$.ajax({
         	  //요청
         	type:'get',
            url: '${contextPath}/event/changePrice.do',
            data: $(this).parent().serialize(),
            // 응답
        	  dataType: 'json',
        	  success : (resData) => {
        		if(resData.PriceResult === 1){
        		  alert('할인가가 변경되었습니다.');
        		  fnReEventList();	
        		} else {
            	  alert('할인가가 변경되지않았습니다.');
            	  return;	
        		}
        	  }
         	})
         })
       }
    
    fnReEventList();
    fnDetailEvent();
    fnEventEnd();
    fnEventStart();
    fnEventWrite();
    fnChangePercent();
    fnChangePrice();
 </script>
  


<%@ include file="../layout/footer.jsp" %>