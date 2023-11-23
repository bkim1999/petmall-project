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
  .ck.ck-editor {
    max-width: 1000px;
  }
  .ck-editor__editable {
    min-height: 400px;
  }
  .ck-content {
    color: gray;
  }
</style>


  <div>이벤트글 작성하로왔니?</div>
  
  <div>
    <form method="post" action="${contextPath}/event/addevent.do" enctype="multipart/form-data">
      <div>
        <label for="title">제목</label>
        <input type="text" name="title" id="title">
      </div>
      
      <div>
        <textarea name="contents" id="contents" class="form-control"></textarea>
      </div>
      
      <div>
        <label for="files" class="form-label">1개의 썸네일 첨부</label>
        <input type="file" id="files" name="files" multiple class="form-control">
      </div>
      
       <div>
        <label for="files" class="form-label">이벤트사진</label>
        <input type="file" name="event_images" id="event_images" class="form-control" multiple>
      </div>
      
      <div>
        <label id="startAt">시작일</label>
        <input type="text" name="startAt"  id="startAt">
        <label id="endAt">종료일</label>
        <input type="text" name="endAt" id="endAt">
      </div>
      
      <div>
        <label id="discountPercent">할인율</label>
        <input type="text" name="discountPercent" id="discountPercent">
      </div>
      
      <div>
        <label id="discountPrice">할인가</label>
        <input type="text" name="discountPrice" id="discountPrice">
      </div>
      
      <hr>
      
      <div>
        <button class="btn btn-primary col-12" type="submit">작성완료</button>
      </div>
      
    </form>
  </div>
<script>
const fnCkeditor = () => {
    ClassicEditor
      .create(document.getElementById('contents'), {
        toolbar: {
          items: [
            'undo', 'redo',
            '|', 'heading',
            '|', 'fontfamily', 'fontsize', 'fontColor', 'fontBackgroundColor',
            '|', 'bold', 'italic', 'strikethrough', 'subscript', 'superscript', 'code',
            '|', 'link', 'uploadImage', 'blockQuote', 'codeBlock',
            '|', 'bulletedList', 'numberedList', 'todoList', 'outdent', 'indent'
          ],
          shouldNotGroupWhenFull: false
       },
       heading: {
         options: [
           { model: 'paragraph', title: 'Paragraph', class: 'ck-heading_paragraph' },
           { model: 'heading1', view: 'h1', title: 'Heading 1', class: 'ck-heading_heading1' },
           { model: 'heading2', view: 'h2', title: 'Heading 2', class: 'ck-heading_heading2' },
           { model: 'heading3', view: 'h3', title: 'Heading 3', class: 'ck-heading_heading3' },
           { model: 'heading4', view: 'h4', title: 'Heading 4', class: 'ck-heading_heading4' },
           { model: 'heading5', view: 'h5', title: 'Heading 5', class: 'ck-heading_heading5' },
           { model: 'heading6', view: 'h6', title: 'Heading 6', class: 'ck-heading_heading6' }
         ]
       },
       ckfinder: {
         // 이미지 업로드 경로
         uploadUrl: '${contextPath}/event/imageUpload.do',
       }
     })
     .catch(err => {
       console.log(err)
     });
  }
  
  
  fnCkeditor();

</script>  
  
  


<%@ include file="../layout/footer.jsp" %>