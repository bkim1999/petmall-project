
const fnToggleList = () => {
  $(document).on('click', '#btn_my_review', function(){
    fnGetReviewList();
  });
  $(document).on('click', '#btn_not_reviewed', function(){
    fnGetNotReviewList();
  });
}

var page = 1;
const fnGetReviewList = () => {
  $.ajax({
    // 요청
    type: 'get',
    url: '/review/getMyReviewList.do',
    data: {'userNo' : userNo
         , 'page' : page
         , 'order' : 'REVIEW_MODIFIED_AT'
          },
    // 응답
    dataType: 'json',
    success: (resData) => {  // resData = {"reviewList": [], "paging": ""}
      $('#review_list').empty();
      $('#review_list').next('nav').remove();
      $('#review_list').prev().text('내 리뷰 목록');
      if(resData.reviewList.length === 0){
        $('#review_list').append('<div class="text-center align-middle">작성한 리뷰가 없습니다.</div>');
        return false;
      }
      $.each(resData.reviewList, function(i, review){
        let str = '';
        str += '<div class="review d-flex mt-3" data-product-no="' + review.productDto.productNo + '">';
        if(review.reviewImageDto !== null){
          str += '<image class="thumbnail" src="' + review.reviewImageDto.path + '/' + review.reviewImageDto.filesystemName + '">';
        } else {
          str += '<image class="thumbnail" src="/image/no_image.png">';
        }
        str += '<div class="review_info">'
        str += '  <h4 class="star">';
        str += '★'.repeat(review.reviewRating) + '☆'.repeat(5 - review.reviewRating);
        str += '  </h4>';
        str += '  <div>' + review.reviewModifiedAt + '</div>';
        str += '  <div>상품명: ' + review.productDto.productName + ' / 옵션: ' + review.productOptionDto.optionName + '</div>';
        if(review.reviewTitle){
          str += '  <h5 class="review_title mt-2 text-break">' + review.reviewTitle + '</h5>'
        }
        if(review.reviewContents){
          str += '  <div class="review_contents mt-2 text-wrap text-break">' + review.reviewContents + '</div>';
        }
        str += '</div>'
        str += '<div class="ms-auto btn-group">';
        str += '  <form class="frm_edit_remove_review" method="post">';
        str += '    <input type="hidden" name="userNo" value="' + userNo + '">';
        str += '    <input type="hidden" name="reviewNo" value="' + review.reviewNo + '">';
        str += '    <input type="hidden" name="productNo" value="' + review.productDto.productNo + '">';
        str += '    <input type="hidden" name="productName" value="' + review.productDto.productName + '">';
        str += '    <input type="hidden" name="optionNo" value="' + review.productOptionDto.optionNo + '">';
        str += '    <input type="hidden" name="optionName" value="' + review.productOptionDto.optionName + '">';
        str += '    <input type="hidden" name="reviewRating" value="' + review.reviewRating + '">';
        if(review.reviewTitle !== null){
          str += '    <input type="hidden" name="reviewTitle" value="' + review.reviewTitle + '">';
        } else {
          str += '    <input type="hidden" name="reviewTitle" value="">';
        }
        if(review.reviewContents !== null){
          str += '    <input type="hidden" name="reviewContents" value="' + review.reviewContents + '">';
        } else {
          str += '    <input type="hidden" name="reviewContents" value="">';
        }
        if(review.reviewImageDto !== null){
          str += '    <input type="hidden" name="reviewImagePath" value="' + review.reviewImageDto.path + '/' + review.reviewImageDto.filesystemName + '">';
        } else {
          str += '    <input type="hidden" name="reviewImagePath" value="">';
        }
        str += '    <button type="button" class="btn btn_edit_review">수정</button>';
        str += '    <button type="button" class="btn btn_remove_review">삭제</button>';
        str += '</div>'
        str += '</div>';
        $('#review_list').append(str);
      });
      $('#review_list').after(resData.paging);
    }
  });
}

const fnGetNotReviewList = () => {
  $.ajax({
    // 요청
    type: 'get',
    url: '/review/getNotReviewedList.do',
    data: {'userNo' : userNo
         , 'page' : page
         , 'order' : 'ORDER_DATE'
          },
    // 응답
    dataType: 'json',
    success: (resData) => {  // resData = {"notReviewedList": [], "paging": ""}
      $('#review_list').empty();
      $('#review_list').next('nav').remove();
      $('#review_list').prev().text('작성하지 않은 리뷰');
      if(resData.notReviewedList.length === 0){
        $('#review_list').append('<div class="text-center align-middle">작성할 리뷰가 없습니다.</div>');
        return false;
      }
      $.each(resData.notReviewedList, function(i, notReviewed){
        let str = '';
        str += '<div class="review mt-3 d-flex justify-content-start" data-product-no="' + notReviewed.productDto.productNo + '">';
        if(notReviewed.reviewImageDto !== null){
          str += '<image class="thumbnail" src="' + notReviewed.reviewImageDto.path + '/' + notReviewed.reviewImageDto.filesystemName + '">';
        } else {
          str += '<image class="thumbnail" src="/image/no_image.png">';
        }
        str += '<div>';
        str += '  <div>구매일자: ' + notReviewed.orderDate + '</div>';
        str += '  <h4 class="mt-3">' + notReviewed.productDto.productName + '</h4>';
        str += '  <div>옵션: ' + notReviewed.productOptionDto.optionName + '</div>';
        str += '</div>'
        str += '<div class="ms-auto">'
        str += '  <form method="post" action="/review/addReview.form">';
        str += '    <input type="hidden" name="userNo" value="' + userNo + '">';
        str += '    <input type="hidden" name="productNo" value="' + notReviewed.productDto.productNo + '">';
        str += '    <input type="hidden" name="productName" value="' + notReviewed.productDto.productName + '">';
        str += '    <input type="hidden" name="optionNo" value="' + notReviewed.productOptionDto.optionNo + '">';
        str += '    <input type="hidden" name="optionName" value="' + notReviewed.productOptionDto.optionName + '">';
        if(notReviewed.reviewImageDto !== null){
          str += '      <input type="hidden" name="productImagePath" value="' + notReviewed.reviewImageDto.path + '/' + notReviewed.reviewImageDto.filesystemName + '">';
        }
        str += '    <button type="submit" class="btn_add_review_form btn ms-auto" id="btn_add_review">리뷰 작성</button>';
        str += '  </form>'
        str += '</div>'
        str += '</div>';
        $('#review_list').append(str);
      });
      $('#review_list').after(resData.paging);
    }
  })
}

function fnRemoveReview(){
  $(document).on('click', '.btn_remove_review', function(ev){
    ev.stopPropagation();
    if(!confirm('이 리뷰를 삭제하시겠습니까?')){
      return;
    }
    $(this).parents('.frm_edit_remove_review').attr('action', '/review/removeReview.do');
    $(this).parents('.frm_edit_remove_review').submit();
  })
}

function fnEditReview(){
  $(document).on('click', '.btn_edit_review', function(ev){
    ev.stopPropagation();
    if(!confirm('이 리뷰를 수정하시겠습니까?')){
      return;
    }
    $(this).parents('.frm_edit_remove_review').attr('action', '/review/editReview.form');
    $(this).parents('.frm_edit_remove_review').submit();
  })
}

const fnAddReviewResult = () => {
  if(addReviewResult !== null){
    if(addReviewResult === true){
      alert('성공적으로 등록되었습니다.');
    } else {
      alert('리뷰 등록에 실패하였습니다.');
    }
  }
}

const fnEditReviewResult = () => {
  if(editReviewResult !== null){
    if(editReviewResult === true){
      alert('성공적으로 수정되었습니다.');
    } else {
      alert('리뷰 수정에 실패하였습니다.');
    }
  }
}

const fnRemoveReviewResult = () => {
  console.log(removeReviewResult);
  if(removeReviewResult !== null){
    if(removeReviewResult == true){
      alert('성공적으로 삭제되었습니다.');
    } else {
      alert('리뷰 삭제에 실패하였습니다.');
    }
  }
}

const fnGoToProduct = () => {
  $(document).on('click', '.review', function(){
    let productNo = $(this).closest('.review').data('product-no');
    location.href = '/product/detail.do?productNo=' + productNo;
  });
}

fnToggleList();
fnGetReviewList();
fnRemoveReview();
fnEditReview();
fnAddReviewResult();
fnEditReviewResult();
fnRemoveReviewResult();
fnGoToProduct();