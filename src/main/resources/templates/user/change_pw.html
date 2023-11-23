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


<div id="search_area">
<h1>비밀번호 분실</h1>
<form  id="frm_change_pw" >
  <div>
    <label for="email" >이메일</label>
    <input type="text"  id="email" name="email">
    <button type="button" id="btn_get_code">인증코드받기</button>
     <div id="msg_email"></div>
  </div>
  
  <!-- 이메일인증  -->
<div>


  
  <div>
      <input type="text" id="verify_code" placeholder="인증코드입력" disabled> 
      <button type="button" id="btn_verify_code" disabled>인증하기</button>
  </div>
  
</div>
  
  
 <div><button id="btn_change_pw" type="button">비밀번호변경</button></div>
  
</form>
</div>
<div id="pw_area">


</div>


    



<!-- 스크립트 영역 -->

<script>


/*****************전역변수 선언*****************************************/
var emailPassed = false;
var pw_area=$('#pw_area');
var search_area=$('#search_area');
var pwPassed = false;
var pw2Passed = false;
/* **************************컨텍스트 패스**************************** */
const getContextPath = () => {
  let begin = location.href.indexOf(location.host) + location.host.length;
  let end = location.href.indexOf('/', begin + 1);
  return location.href.substring(begin, end);
}

/* ************************* 이메일 인증코드  ****************************** */
/* 이메일 정규식 체크 */
const fnCheckEmail = () =>{
	
	
	$('#btn_get_code').click(()=>{
		let email =$('#email').val();
		
		
		//정규식 통과 -> 이메일 중복체크 통과 -> 인증코드발송// 
		
		 new Promise((resolve, reject)=> { 
			
			// 정규식 조건 
			//(영어대소문자,숫자) @ (영어대소문자,숫자 2자리 이상으로 구성) (.(영어대소문자 2자리이상 6자리 이하로 구성))이 1회이상 2회이하로 반복구성되는것으로 끝나게 구성.
			let regEmail= /^[A-Za-z0-9-_]+@[A-Za-z0-9]{2,}([.][A-Za-z]{2,6}){1,2}$/;
			
			//정규식 검사
			if(!regEmail.test(email)){
				reject(1);
				return;
			}
			
			

    	// 인증코드 전송
    	$.ajax({
				
				//요청
				type:'get',
				url:getContextPath() +'/user/sendCode.do',
				data:'email='+email,
				
				//응답
				dataType:'json',
				success:(resData)=>{
					
					alert(email + "로 인증코드를 전송했습니다."); 
					$('#verify_code').prop('disabled', false);
					$('#btn_verify_code').prop('disabled', false);
					$('#btn_verify_code').click(()=>{
						emailPassed =$('#verify_code').val() === resData.code;
						if(emailPassed){
							alert('이메일이 인증되었습니다.');
						}
						else {
							alert('이메일 인증이 실패했습니다.');
						}
						
					})
				}
				
			})
    	
	//  인증 실패시 reject번호에 따라서 동작할것들 
    }).catch((state)=>{
       emailPassed = false;
       switch(state){
       case 1: $('#msg_email').text('이메일 형식이 올바르지 않습니다.'); break;
    }
		
		
      })
    })
	
}





/****************************비밀번호변경용 이메일 검색**********************************/
const fnChangePw=()=>{
	$('#btn_change_pw').click(()=>{
		
		var str='';
		pw_area.empty();
		
		
		$.ajax({
			type:'post',
			url:getContextPath() +'/user/change_pw.do',
			data:$('#frm_change_pw').serialize(),
			
			
			dataType:'json',
			success:(resData)=>{
				
				if(resData.result==1&&emailPassed==true)
				{
					// 기존 이메일 검색 화면 지우기
					search_area.hide();
					
					
					//새 비번 설정 폼 보여주기
					str+='<h1>비밀번호 변경</h1>'
					str+=' <form action="${contextPath}/user/modify_pw.do" method="post" id="frm_pw">';	
					str+='   <div>';	
					str+='     <input type="password" id="pw" name="pw" placeholder="새 비밀번호">  ';
					str+='     <div id="msg_pw"></div>'; 
					str+='   </div>';	
					str+='   <div>';	
					str+='     <input type="password" id="pw2" placeholder="새 비밀번호 확인">';	
					str+='     <div id="msg_pw2"></div>'; 
					str+='   </div>';	
					str+=' <input type="hidden" id="valid_email" name="valid_email" value="'+$('#email').val()+'">'
					str+='  <button  id="btn_submit_pw">변경적용</button>';	
					str+=' </form>';	
					
					pw_area.append(str);
					
				}else if(resData.result==1&&emailPassed==false) {
					alert('이메일 인증이 필요합니다');
				}
				
				else if(resData.result==0) {
					alert('일치하는 회원정보가 없습니다.');
				}
			

				
				
				
			}
		})
	})
}


/* ******************************비번 정규식 체크/ 비번 일치여부 확인 ****************************** */
 //비번 정규식 확인 
 const fnCheckPassword = () => {
	  $(document).on('keyup','#pw',(ev) => {
	    let pw = $(ev.target).val();
	    // 비밀번호 : 8~20자, 영문,숫자,특수문자, 2가지 이상 포함
	    let validPwCount = /[A-Z]/.test(pw)          // 대문자가 있으면   true
	                     + /[a-z]/.test(pw)          // 소문자가 있으면   true
	                     + /[0-9]/.test(pw)          // 숫자가 있으면     true
	                     + /[^A-Za-z0-9]/.test(pw);  // 특수문자가 있으면 true
	    pwPassed = pw.length >= 8 && pw.length <= 20 && validPwCount >= 2;
	    if(pwPassed){
	      $('#msg_pw').text('사용 가능한 비밀번호입니다.');
	    } else {
	      $('#msg_pw').text('비밀번호는 8~20자, 영문/숫자/특수문자를 2가지 이상 포함해야 합니다.');       
	    }
	  })
	}
 
// 비번 일치여부 확인 
	const fnCheckPassword2 = () => {
	  $(document).on('blur','#pw2',(ev) => {
	    let pw = $('#pw').val();
	    let pw2 = ev.target.value;
	    pw2Passed = (pw !== '') && (pw === pw2);
	    if(pw2Passed){
	      $('#msg_pw2').text('');
	    } else {
	      $('#msg_pw2').text('비밀번호 입력을 확인하세요.');
	    }
	  })
	}

/*************************비번변경폼 제츨****************************/	
const fnSubmitPw=()=>{
	$(document).on('click','#btn_submit_pw',(ev)=>{
		
		if(!pwPassed || !pw2Passed){
      		alert('비밀번호를 확인하세요.');
      				ev.preventDefault();
      				return;
      	}
	})	
	
}
	
	
	
	
/***************************호출***********************************/  
fnSubmitPw();
fnCheckPassword();
fnCheckPassword2();
fnChangePw();
fnCheckEmail();
</script>


</div>



<%@ include file="../layout/footer.jsp" %>


    

