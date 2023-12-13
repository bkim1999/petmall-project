// 전역 변수
var page = 1;
var order = 'PRODUCT_SALES';
var totalPage = 0;
var searchText = '';

// 카테고리 목록
const fnGetCategoryList = () => {
  $.ajax({
    // 요청
    type: 'get',
    url: '/product/getCategoryList.do',
    // 응답
    dataType: 'json',
    success: (resData) => {  // resData = {"categoryList": []}
      // 카테고리 목록이 없을 경우: 버튼을 만들지 않는다
      if(resData.categoryList === null){
        $('#category_btn_list').text('카테고리 준비 중...');
        return;
      }
      // 카테고리 목록이 있을 경우: 버튼 생성
      $.each(resData.categoryList, function(i, category){
        let str = '<li class="nav-item category" role="presentation" data-category-no="' + category.categoryNo + '">';
        if(i !== categoryNo){
          str += '  <a class="nav-link" data-bs-toggle="tab" aria-selected="false" role="tab">' + category.categoryName + '</a>';
        } else {
          str += '  <a class="nav-link active" data-bs-toggle="tab" aria-selected="false" role="tab">' + category.categoryName + '</a>';
        }
        str += '</li>';
        $('#category_list').append(str);
      })
    }
  });
}
  
// 카테고리 버튼 클릭 시 data에서 카테고리번호 가져오기
const fnGetCategoryProductList = () => {
  $(document).on('click', '.category', function(){
    page = 1;
    $('#product_list').empty();
    categoryNo = $(this).data('category-no');
    fnGetProductList();
  });
}
  
// 해당 카테고리 상품목록
const fnGetProductList = () => {
  $.ajax({
    // 요청
    type: 'get',
    url: '/product/getProductList.do',
    data: {'categoryNo' : categoryNo,
           'page' : page,
           'order' : order,
           'searchText' : searchText
          },
    // 응답
    dataType: 'json',
    success: (resData) => {  // resData = {"productList": [], "totalPage": 10, "event": 이벤트정보}
    
      // 상품 목록이 없을 경우: 반환된 메세지 알림
      if(resData.productList === null){
        $('#product_list').text(resData.message);
        return;
      }
      // 상품 목록이 있을 경우: 화면에 출력
      totalPage = resData.totalPage;
      $.each(resData.productList, (i, product) => {
        let str = '<div class="card product" data-product-no="' + product.productNo + '">';         
        str += '  <div class="product_thumbnail d-flex justify-content-center align-items-center">';
        if(product.productImageDto === null){
          str += '    <img class= "product-img" src="/image/no_image.png">';
        } else {
          str += '    <img class= "product-img" src="' + product.productImageDto.path + '/' + product.productImageDto.filesystemName + '">';
        }
        if(product.productCount === 0) {
          str += '    <div class="card-img-overlay">품절</div>';
        } else if(product.productCount < 10) {
          str += '    <div class="card-img-overlay">' + product.productCount + '개 남음</div>';
        }
        str += '  </div>';
        str += '  <div class="card-body">';
        str += '  <h4 class="card-title">' + product.productName + '</h4>';
        str += '  <h6 class="card-subtitle text-muted">' + product.productTitle + '</h6>';
        str += '  <div class="price_info" class="d-flex">';
        str += '    <span class="product_sale_price"><strong>' + (Math.floor(product.productPrice * (100 - resData.event.discountPercent) / 100)) + '</strong>원</span>';
        str += '    <span class="product_price"><strong>' + product.productPrice + '</strong>원</span>';
        str += '    <span class="discount_percent"><strong>' + resData.event.discountPercent + '</strong>%</span>';
        str += '  </div>';
        if(product.reviewCount === 0){
          str += '  <p class="card-text">아직 리뷰가 없습니다.</p>';
        } else {
          str += '  <p class="card-text"><strong class="star">' + '★'.repeat(Math.floor(product.productRating)) + '☆'.repeat(5 - Math.floor(product.productRating)) + '</strong>  ' + product.reviewCount + '개의 리뷰</p>';
        }
        str += '  </div>';
        str += '</div>';      
        $('#product_list').append(str);
      });
    }
  })
}

// 검색 버튼 클릭 시 상품 목록 다시 가져오기
const fnSearchProduct = () => {
  $(document).on('click', '#btn_search', function(){
    page = 1;
    searchText = $.trim($('#searchText').val());
    $('#product_list').empty();
    fnGetProductList();
  });
}

// 정렬 버튼 클릭 시 상품 목록 정렬 후 다시 가져오기
const fnOrderProduct = () => {
  $(document).on('click', '.btn-check', function(){
    page = 1;
    order = $(this).val();
    $('#product_list').empty();
    fnGetProductList();
  });
}

const fnScroll = () => {
  var timerId;
  $(window).on('scroll', (ev) => {
    if(timerId){
      clearTimeout(timerId);
    }
    timerId = setTimeout(() => {
      
      let scrollTop = $(ev.target).scrollTop();
      let windowHeight = $(ev.target).height();
      let documentHeight = $(document).height();
      
      if(scrollTop + windowHeight + 50 >= documentHeight){
        if(page >= totalPage){
          return;
        }
        page++;
        fnGetProductList();
      }
    }, 500);
  })
}

const fnRemoveProductResult = () => {
  if(removeProductResult !== null){
    console.log(removeProductResult)
    if(removeProductResult == true){
      alert('성공적으로 삭제되었습니다.');
    } else {
      alert('상품 삭제에 실패하였습니다.');
    }
  }
}

const fnDetail = () => {
  $(document).on('click', '.product', function(){
    location.href = '/product/detail.do?productNo=' + $(this).data('product-no');
  });
}

fnGetCategoryList();
fnGetProductList();
fnGetCategoryProductList();
fnSearchProduct();
fnOrderProduct();
fnScroll();
fnRemoveProductResult();
fnDetail();
  