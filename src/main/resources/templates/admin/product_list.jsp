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

<script>
// 전역 변수
var categoryNo = 0;
var page = 1;
var order = 'PRODUCT_NAME';
var ascDesc = 'ASC';
var totalPage = 0;

const fnGetProductDetailList = () => {
  $.ajax({
    // 요청
    type: 'get',
    url: '${contextPath}/admin/getlist.do',
    data: {'categoryNo' : categoryNo,
           'page' : page,
           'order' : order,
           'ascDesc' : ascDesc
          },
    // 응답
    dataType: 'json',
    success: (resData) => {  // resData = {"uploadList": [], "totalPage": 10}
      if(resData.productList === null){
        alert(resData.message);
        return;
      }
      totalPage = resData.totalPage;
      $.each(resData.productList, (i, product) => {
    	let str = '<tr>'
    	str += '<td class="product" data=product_no="'+product.productNo+'">'+product.productNo+'</td>';
        str += '<td>' + product.productName+'</td>';
        str += '<td>' + product.categoryNo + '</td>';
        str += '<td class="bseq_ea">' + product.productCount + '</td>';
        str += '<td>'
        str += '<input type="button" value=" - " onclick="fndel()">'
        str += '<input type="text" class="amounts" value="'+product.productCount+'" readonly="readonly" style="text-align:center;"/>'
        str += '<input type="button" value=" + " onclick="fnadd()">'
        str += '</td>'
        str += '<td>'
        str += '<input type="button" value="변경하기">'
        str += '</td>'
        str += '</tr>'
        $('#product_list').append(str);
      });
    }
  })
}

const fnadd = () =>{
  
}


   fnGetProductDetailList();
   fnadd();
   
</script>

  <style>
    .btn_admin{
      font-size : 30px;
      text-align: center;
    }
    .table{
      display:flex;
      text-align: center;
    }
  </style>

  <div class="btn_admin">재고 관리 페이지에 오신것을 환영합니다.</div>
  <div>
   <a href="${contextPath}/product/addProduct.form">
    <input type="button" value="제품추가하기">
   </a>
  </div>
  
  <div class="table">
    <table border=1>
     <thead>
      <tr>
        <td>제품 번호</td>
        <td>제품 이름</td>
        <td>제품 카테고리</td>
        <td>재고 현황</td>
        <td>제품 수량조절</td>
        <td>수량 변경하기</td>
      </tr>
     </thead>
     <tbody id="product_list">
     
     </tbody>
    </table>
  </div>
  <div>
    <a href="${contextPath}/admin/admin.go"><input type="button" value="관리자페이지돌아가기"></a>
  </div>


<%@ include file="../layout/footer.jsp" %>