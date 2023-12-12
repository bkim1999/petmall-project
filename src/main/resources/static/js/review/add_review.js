const fnToggleValid = (input, isInvaild, feedback) => {
  if(isInvaild){
    if(!input.hasClass('is-invalid')){
      input.removeClass('is-valid');
      input.addClass('is-invalid');
    }
    input.siblings('.invalid-feedback').text(feedback);
  } else {
    if(!input.hasClass('is-valid')){
      input.removeClass('is-invalid');
      input.addClass('is-valid');
    }
  }
}

const fnFileCheck = () => {
  $('#thumbnail, #product_images').change((ev) => {
    let maxSize = 1024 * 1024 * 100;
    let maxSizePerFile = 1024 * 1024 * 10;
    let totalSize = 0;
    let files = ev.target.files;
    for(let i = 0; i < files.length; i++){
      totalSize += files[i].size;
      if(files[i].size > maxSizePerFile){
        alert('각 첨부파일의 최대 크기는 10MB입니다.');
        $(ev.target).val('');
        return;
      }
    }
    if(totalSize > maxSize){
      alert('전체 첨부파일의 최대 크기는 100MB입니다.');
      $(ev.target).val('');
      return;
    }
  });
}



// '새 이미지 추가' 요소 클릭 시 파일 input 클릭
const fnOpenFileSelect = () => {
  $(document).on('click', '.add-image-area', function(){
    $(this).siblings('.image-input').click();
  });
}

// index와 이미지 src를 받아 출력하는 함수
const fnPreviewImage = (index, imageSrc, target) =>{
  let str = '<div class="preview-image-area" index="' + index + '" draggable="true">';
  str += '  <img class="preview-image" src="' + imageSrc + '" draggable="false">';
  str += '  <div class="btn_delete_image" style="height: 30px;">X</div>';
  str += '</div>';
  target.before(str);
}

const fnAddReviewImage = () => {
  $(document).on('change', '#review_image_area .image-input', function(ev){
    let target = $('#review_image_area .add-image-area');
    console.log(target);
    fnPreviewImage(-1, URL.createObjectURL($(this)[0].files[0]), target);
    target.remove();
    fnToggleValid($('#thumbnail_area'), false, '');
  });
}

const fnDeleteReviewImage = () => {
  $(document).on('click', '#review_image_area .btn_delete_image', function(ev){
    if(!confirm('이 이미지를 삭제하시겠습니까?')){
      return;
    }
    let str = '<div class="add-image-area">';
    str += '  <img class="preview-image" src="/image/plus_sign.png">';
    str += '  <div class="text-center mb-2" style="height: 30px;">새 이미지 추가</div>';
    str += '</div>';
    $(this).parents('.preview-list').append(str);
    $(this).parent().siblings('.image-input').val('');
    $(this).parent().remove();
  });
}

fnAddReviewImage();
fnDeleteReviewImage();

const fnCheckInput = () => {
  $(document).on('keyup', 'input[type="text"], textarea', function(){
    fnToggleValid($(this), $(this).val().length > 200, ' * 문자수는 최대 200자입니다.');
  });
}

const fnCheckRequired = () => {
  $(document).on('click', '#btn_add_review', function(){
    
    // 평점 입력 여부 체크
    if($('#reviewRating').val() == 0){
      alert('평점을 입력해주세요.');
      return;
    }
    
    // 글자수 체크
    $.each($('input[type="text"], textarea'), (i, input) => {
      fnToggleValid($(input), $(input).val().length > 200, ' * 문자수는 최대 200자입니다.');
    });
    
    // 이미지 첨부항목 첨부파일 유형이 이미지인지 체크
    var files = Array.from($('#review_image').prop('files'));
    if(files.length !== 0){
      for(let i = 0; i < files.length; i++){ // 파일유형이 이미지인지 체크
        let filetype = files[i].type;
        let input = (i === 0) ? $('#thumbnail') : $('#product_images');
        if(filetype.substring(0, filetype.indexOf('/')) !== 'image') {
          fnToggleValid(input, true, ' * 이미지 파일만 첨부할 수 있습니다.');
          break;
        }
      }
    }
    
    // 공백/초과인 입력칸이 있을 경우 submit 안함
    if($(".not-empty").is(".is-invalid")){
      alert('아직 작성되지 않았거나 잘못된 입력사항이 있습니다.')
      return;
    }
    
    // 문제 없으면 SUBMIT
    $(this).parents('form').submit();
  });
}

const fnSetRating = () => {
  $(document).on('click', '.star', function(){
    $(this).prevAll('.star').text('★');
    $(this).text('★');
    $(this).nextAll('.star').text('☆');
    $('#reviewRating').val($(this).prevAll('.star').length + 1);
  })
}

const fnGoToProduct = () => {
  $(document).on('click', '.product-info', function(){
    location.href = '/product/detail.do?productNo=' + productNo;
  });
}


fnFileCheck();
fnOpenFileSelect();

fnCheckInput();
fnCheckRequired();

fnSetRating();
fnGoToProduct();