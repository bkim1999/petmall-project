$(document).ready(function(){
  $('.carousel-item').first().addClass('active');
});

var cartCount = 0;

// 선택한 옵션 출력
function fnAddOption(){
  $(document).on('change', '#option_list', function() {
    var optionNo = $(this).val();
    if(optionNo === '0'){
      return;
    }
    var selected_optionNo_list = [];
    $.each($('#selected_option_list').children(), function(i, option) {
      selected_optionNo_list[i] = $(option).data('option-no');
    });
    if ($.inArray(parseInt(optionNo), selected_optionNo_list) !== -1){
      alert('이미 추가된 옵션입니다.');
      $(this).val("0");
      return;
    }
    let optionName = $("#option_list option:selected").text();
    let addPrice = $("#option_list option:selected").data('add-price');
    let optionCount = $("#option_list option:selected").data('option-count');
    console.log(optionCount);
    let str = '<div class="selected_option d-flex justify-content-between align-items-center" data-option-no="' + optionNo + '">';
    str += '  <div class="option_name">' + optionName + '</div>';
    str += '  <div class="d-flex pt-2 option_count">';
    str += '    <button type="button" class="btn btn-light minus_count">-</button>';
    str += '    <input type="hidden" name="cartList[' + cartCount + '].userDto.userNo" value="' + userNo + '">';
    str += '    <input type="hidden" class="option_no" name="cartList[' + cartCount + '].productOptionDto.optionNo" value="' + optionNo + '">';
    str += '    <input type="text" class="count form-control-plaintext text-center" value="1" name="cartList[' + cartCount + '].count" data-option-count="' + optionCount + '" readonly>';
    str += '    <button type="button" class="btn btn-light plus_count">+</button>';
    str += '    <button type="button" class="btn btn-light btn_delete_option">X</button>';
    str += '  </div>'
    str += '  <input type="hidden" class="option_price" value="' + Math.floor(productPrice * (100 - discountPercent) / 100 + addPrice) + '">';
    str += '  <div class="calculated_price_wrapper"><span class="calculated_price">' + Math.floor(productPrice * (100 - discountPercent) / 100 + addPrice) + '</span>원</td>';
    str += '</div>';
    $('#selected_option_list').append(str);
    $(this).val("0");
    cartCount += 1;
  });
}

// 선택한 옵션 삭제
const fnDeleteOption = () => {
  $(document).on('click', '.btn_delete_option', function(){
    $(this).parents('.selected_option').remove();
    fnGetTotalPrice();
  });
}

// 옵션 수량 감소
const fnDecreaseCount = () => {
  $(document).on('click', '.minus_count', function(){
    var count = $(this).siblings('.count');
    if(count.val() === '1'){
      alert('선택한 옵션의 개수는 1개 이상이어야 합니다.');
      return;
    }
      count.val(parseInt(count.val()) - 1); // 화면에 보이는 개수 감소
      var calculated_price = $(this).parent().siblings('.calculated_price_wrapper').find('.calculated_price'); // 계산된 가격
      var option_price = $(this).parent().siblings('.option_price'); // 옵션 가격
      var count = $(this).siblings('.count'); // 개수
      // 계산된 가격 = option_price(옵션가격) * count(개수)
      calculated_price.text(option_price.val() * count.val()).trigger('change');
  });
}

// 옵션 수량 증가
const fnIncreaseCount = () => {
  $(document).on('click', '.plus_count', function(){
      var count = $(this).siblings('.count');
      if(count.val() >= count.data('option-count')){
        alert('해당 옵션의 재고가 부족합니다.');
        return;
      }
      count.val(parseInt(count.val()) + 1); // 화면에 보이는 개수 증가
      var calculated_price = $(this).parent().siblings('.calculated_price_wrapper').find('.calculated_price'); // 계산된 가격
      var option_price = $(this).parent().siblings('.option_price'); // 옵션 가격
      var count = $(this).siblings('.count'); // 개수
      // 계산된 가격 = option_price(옵션가격) * count(개수)
      calculated_price.text(option_price.val() * count.val()).trigger('change');
  });
}

const fnGetTotalPrice = () => {
  var total = 0;
  $.each($('.calculated_price'), (i, price) => {
    total += parseInt($(price).text());
  });
  $('#total_price').text(total);
}

const fnUpdateTotalPrice = () => {
  $(document).on('change', '#option_list, .calculated_price', function(){
    fnGetTotalPrice();
  });
}

const fnAddCartOrder = () => {
  $(document).on('click', '#btn_add_cart', function(){
    $('#frm_cart_order').attr('th:action', '@{/order/addCart.do}');
    $('#frm_cart_order').submit();
  })
  $(document).on('click', '#btn_add_order', function(){
    $('#frm_cart_order').attr('th:action', '@{/order/addOrder.do}');
    $('#frm_cart_order').submit();
  })
}


// 상품명
var page = 1;
var order = 'REVIEW_CREATED_AT';
const fnGetReviewList = () => {
  $.ajax({
    // 요청
    type: 'get',
    url: '/review/getReviewList.do',
    data: {'productNo' : productNo
         , 'page' : page
         , 'order' : order
          },
    // 응답
    dataType: 'json',
    success: (resData) => {  // resData = {"reviewList": [], "paging": ""}
      if(resData.reviewList === null){
        alert('리뷰 목록 불러오기 실패');
        return;
      }
      if(resData.reviewList.length === 0){
        $('#review_list').text('아직 리뷰가 없습니다.');
        return;
      }
      $.each(resData.reviewList, (i, review) => {
        let str = '<div class="review d-flex" data-review-no="' + review.reviewNo + '">';
        str += '<div>'
        str += '  <h4 class="star">';
        str += '★'.repeat(review.reviewRating) + '☆'.repeat(5 - review.reviewRating);
        str += '  </h4>';
        str += '  <div>' + review.userDto.name + '  ' + review.reviewModifiedAt + '</div>';
        str += '  <div>상품명: ' + productName + ' / 옵션: ' + review.productOptionDto.optionName + '</div>';
        if(review.reviewTitle !== null){
          str += '  <h5 class="review_title mt-2 text-break">' + review.reviewTitle + '</h5>'
        }
        if(review.reviewContents !== null){
          str += '  <div class="review_contents text-break">' + review.reviewContents + '</div>';
        }
        str += '</div>'
        if(review.reviewImageDto !== null){
          str += '<image class="review_image ms-auto" src="' + review.reviewImageDto.path + '/' + review.reviewImageDto.filesystemName + '">';
        } else {
          str += '<image class="review_image ms-auto" src="/image/no_image.png">';
        }
        str += '</div>';
        $('#review_list').append(str);
      });
      $('#review_list').append(resData.paging);
    }
  })
}

function fnEditProduct(){
  $(document).on('click', '#btn_edit_product', function(){
    if(!confirm('이 제품을 수정하시겠습니까?')){
      return;
    }
    let form = $(this).parents('form');
    $(form).attr('method', 'get');
    $(form).attr('action', '/product/editProduct.form');
    $(form).submit();
  });
}

function fnRemoveProduct(){
  $(document).on('click', '#btn_remove_product', function(){
    if(!confirm('이 제품을 삭제하시겠습니까?')){
      return;
    }
    let form = $(this).parents('form');
    $(form).attr('method', 'post');
    $(form).attr('action', '/product/removeProduct.do');
    $(form).submit();
  });
}

const fnEditProductResult = () => {
  if(editProductResult !== null){
    if(editProductResult === true){
      alert('성공적으로 수정되었습니다.');
    } else {
      alert('상품 수정에 실패하였습니다.');
    }
  }
}

fnAddOption();
fnDeleteOption();
fnDecreaseCount();
fnIncreaseCount();
fnUpdateTotalPrice();
fnAddCartOrder();
fnGetReviewList();
fnEditProduct();
fnRemoveProduct();
fnEditProductResult();