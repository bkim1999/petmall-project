var editor;
const fnCkeditor = () => {
  ClassicEditor
    .create(document.getElementById('productContents'), {
      toolbar: {
        items: [
          'undo', 'redo',
          '|', 'uploadImage'
        ],
        shouldNotGroupWhenFull: false
     },
     ckfinder: {
       // 이미지 업로드 경로
       uploadUrl: '/product/imageUpload.do',
     }
   })
   .then(newEditor => {
     editor = newEditor;
   })
   .catch(err => {
     console.log(err)
   });
}

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

let optionCount = 1; // 현재 옵션 개수

const fnAddOption = () => {
  $('#btn_add_option').click((ev) => {
    let str = '';
    if(optionCount === 5){
      str += '<div class="alert alert-dismissible alert-danger mt-5">';
      str += '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
      str += '옵션은 최대 5개입니다.</div>';
      $('#option_list').append(str);
      return;
    }
    str += '<div class="option">';
    str += '  <div class="form-group">';
    str += '    <label class="form-label" for="productOptionList[' + optionCount + '].optionName">옵션명</label>';
    str += '    <input type="text" name="productOptionList[' + optionCount + '].optionName" id="productOptionList[' + optionCount + '].optionName" class="not-empty form-control">';
    str += '    <div class="invalid-feedback"></div>';
    str += '  </div>';
    str += '  <div class="form-group">';
    str += '    <label class="form-label mt-4" for="productOptionList[' + optionCount + '].addPrice">추가금액</label>';
    str += '    <div class="input-group mb-3">';
    str += '      <span class="input-group-text">\\</span>';
    str += '      <input type="text" name="productOptionList[' + optionCount + '].addPrice" id="productOptionList[' + optionCount + '].addPrice" class="not-empty only-number form-control">';
    str += '      <span class="input-group-text">원</span>';
    str += '      <div class="invalid-feedback"></div>';
    str += '    </div>';
    str += '  </div>';
    str += '  <div class="form-group">';
    str += '    <label class="form-label mt-4" for="productOptionList[' + optionCount + '].optionCount">재고</label>';
    str += '    <div class="input-group mb-3">';
    str += '      <input type="text" name="productOptionList[' + optionCount + '].optionCount" id="productOptionList[' + optionCount + '].optionCount" class="not-empty only-number form-control">';
    str += '      <span class="input-group-text">개</span>';
    str += '      <div class="invalid-feedback"></div>';
    str += '    </div>';
    str += '  </div>';
    str += '  <div>';
    str += '    <button type="button" class="btn btn-danger btn_remove_option mt-3">옵션 삭제</button>';
    str += '  </div>'
    str += '</div>';
    $('#btn_add_option').before(str);
    optionCount = optionCount + 1;
  });
}

const fnRemoveOption = () => {
  $(document).on('click', '.btn_remove_option', function(){
    if(optionCount === 1) {
      alert('옵션은 반드시 1개 이상이어야 합니다.');
      return;
    }
    if(confirm('이 옵션을 삭제하시겠습니까?')){
      $(this).parents('.option').remove();
      optionCount -= 1;
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

const fnAddThumbnail = () => {
  $(document).on('change', '#thumbnail_area .image-input', function(ev){
    // 첨부파일이 이미지가 아닐 시
    if($(this)[0].files[0].type.split('/')[0] !== 'image'){
      $(this).val('');
      alert('이미지 파일만 추가해주세요.');
      return false;
    }
    let target = $('#thumbnail_area .add-image-area');
    fnPreviewImage(-1, URL.createObjectURL($(this)[0].files[0]), target);
    target.remove();
    fnToggleValid($('#thumbnail_area'), false, '');
  });
}

const fnDeleteThumbnail = () => {
  $(document).on('click', '#thumbnail_area .btn_delete_image', function(ev){
    if(!confirm('이 이미지를 삭제하시겠습니까?')){
      return;
    }
    deleteThumbnail = true;
    let str = '<div class="add-image-area">';
    str += '  <img class="preview-image" src="/image/plus_sign.png">';
    str += '  <div class="text-center mb-2" style="height: 30px;">새 이미지 추가</div>';
    str += '</div>';
    $(this).parents('.preview-list').append(str);
    $(this).parent().siblings('.image-input').val('');
    $(this).parent().remove();
    fnToggleValid($('#thumbnail_area'), true, ' * 썸네일은 필수 입력 항목입니다.');
  });
}

fnAddThumbnail();
fnDeleteThumbnail();


var MIN_IMAGE_COUNT = 1;
var MAX_IMAGE_COUNT = 5;
const orderedImageList = [];

// 썸네일을 제외한 상품사진 개수 체크
const fnCheckCurrentImageCount = (fileCount) => {
  let imageCount = orderedImageList.length + fileCount;
  return imageCount < MIN_IMAGE_COUNT || imageCount > MAX_IMAGE_COUNT;
}


// 파일 input에서 이미지 추가 시 실행
const fnAddImageToList = () => {
  $(document).on('change', '#product_images_area .image-input', function(){
    // 등록할 이미지 개수가 0이거나 5 이상일 시
    if(fnCheckCurrentImageCount($(this)[0].files.length)){
      alert('상품사진은 최소 1개, 최대 5개입니다.');
      return;
    };
    
    // 추가 파일이 이미지 파일인지 체크
    let isNotImage = false;
    $.each($(this)[0].files, function(i, image){
      // 첨부파일이 이미지가 아닐 시
      if(image.type.split('/')[0] !== 'image'){
        $('#product_images_area .image-input').val('');
        alert('이미지 파일만 추가해주세요.')
        isNotImage = true;
        return false;
      }
    });
    if(isNotImage){
      return;
    }
    // 이미지 파일 orderedImageList에 저장
    $.each($(this)[0].files, function(i, image){
      orderedImageList.push(image);
      fnPreviewImage(orderedImageList.length - 1, URL.createObjectURL(image), $('#product_images_area .add-image-area'));
    });
    $(this).val('');
    if(orderedImageList.length >= MAX_IMAGE_COUNT){
      $('#product_images_area .add-image-area').remove();
    }
    fnToggleValid($('#product_images_area'), orderedImageList.length < MIN_IMAGE_COUNT, '');
  })
}

// 업로드 예정의 이미지들 출력
const fnPreviewImageList = () => {
  let target = $('#product_images_area .add-image-area');
  $(target).siblings('.preview-image-area').remove();
  $.each(orderedImageList, function(i, image){
    // DB 이미지 정보일 경우
    if(image.imageCode !== undefined){
      fnPreviewImage(i, image.path + '/' + image.filesystemName, target);
    } else {
      fnPreviewImage(i, URL.createObjectURL(image), target);
    }
  });
}

const fnDeleteImage = () => {
  $(document).on('click', '#product_images_area .btn_delete_image', function(ev){
    ev.stopPropagation();
    if(!confirm('이 이미지를 삭제하시겠습니까?')){
      return;
    }
    let imageArea = $(this).parent();
    let index = imageArea.attr('index');
    orderedImageList.splice(index, 1);
    fnPreviewImageList();
    if(orderedImageList.length === MAX_IMAGE_COUNT - 1) {
      let str = '<div class="add-image-area">';
      str += '  <img class="preview-image" src="/image/plus_sign.png">';
      str += '  <div class="text-center mb-2" style="height: 30px;">새 이미지 추가</div>';
      str += '</div>';
      $('#product_images_area > .preview-list').append(str);
    }
    fnToggleValid($('#product_images_area'), orderedImageList.length < MIN_IMAGE_COUNT, ' * 상품사진은 최소 1개, 최대 5개입니다.');
  });
}




  
const fnCheckInput = () => {
  // ckeditor 칸에 이미지 추가 시 이미지가 1개 이상, 10개 이하인지 체크
  $(document).on('change', editor, function() {
    if($('.ck-editor__editable').find('img').length === 0){
      return;
    }
    let isImageCountInvaild = $('.ck-editor__editable').find('figure').length > 10;
    fnToggleValid($('.ck-editor'), isImageCountInvaild, ' * 이미지는 최소 1개, 최대 10개입니다.');
  });
  // ckeditor 칸에 이미지 삭제 혹은 문자 입력/삭제 시 문자수가 200자 이하인지 체크
  $(document).on('keyup', '.ck-editor__editable', function() {
    let isImageCountInvaild = $('.ck-editor__editable').find('img').length === 0 || $('.ck-editor__editable').find('figure').length > 10;
    let isCharCountInvalid = $('.ck-editor__editable').text().length > 200
    fnToggleValid($('.ck-editor'), isImageCountInvaild || isCharCountInvalid, ' * 이미지는 최소 1개, 최대 10개이며 글자수는 최대 200자입니다.');
  });
  $(document).on('keyup', '.not-empty', function(){
    fnToggleValid($(this), $.trim($(this).val()) === '' || $(this).val().length > 200, ' * 문자수는 최소 1자, 최대 200자입니다.');
  });
  $(document).on('change', 'select', function(){
    fnToggleValid($(this), $(this).val() === '', ' * 카테고리를 선택해주세요.');
  });
  $(document).on('keyup', '.only-number', function(){
    if(!/^[0-9]+$/.test($(this).val()) || $(this).val().length > 30){
      fnToggleValid($(this), true, ' * 최소 1개, 최대 30개의 숫자만 입력할 수 있습니다.');
      $(this).val('');
    }
  });
}

const fnCheckRequired = () => {
  $(document).on('click', '#btn_add_product', function(){

    // ckeditor 글자수/ 이미지수 체크
    let isImageCountInvaild = $('.ck-editor__editable').find('img').length === 0 || $('.ck-editor__editable').find('figure').length > 10;
    let isCharCountInvalid = $('.ck-editor__editable').text().length > 200
    fnToggleValid($('.ck-editor'), isImageCountInvaild || isCharCountInvalid, ' * 이미지는 최소 1개, 최대 10개이며 글자수는 최대 200자입니다.');
  
    // 카테고리 체크
    fnToggleValid($('select'), $('select').val() === '', ' * 카테고리를 선택해주세요.');
  
    // 필수항목 글자수 체크
    $.each($('.not-empty'), (i, input) => {
      fnToggleValid($(input), $.trim($(input).val()) === '' || $(input).val().length > 200, ' * 문자수는 최소 1자, 최대 200자입니다.');
    });
    
    
    // 숫자항목 숫자 외 문자 입력과 길이 체크
    $.each($('.only-number'), (i, input) => {
      fnToggleValid($(input), !/^[0-9]+$/.test($(input).val()) || $(input).val().length > 30, ' * 최소 1개, 최대 30개의 숫자만 입력할 수 있습니다.');
    });
    
    // 썸네일 이미지가 입력되었는지 체크
    fnToggleValid($('#thumbnail_area'), $('#thumbnail')[0].files.length === 0, ' * 썸네일은 필수 입력 항목입니다.');
    
    fnToggleValid($('#product_images_area'), orderedImageList.length < MIN_IMAGE_COUNT || orderedImageList.length > MAX_IMAGE_COUNT, ' * 상품사진은 최소 1개, 최대 4개입니다.');
    
    
    // 이미지 첨부항목 첨부파일 유형이 이미지인지 체크 
    var files = Array.from($('#thumbnail').prop('files')).concat(Array.from($('#product_images').prop('files')));
    for(let i = 0; i < files.length; i++){ // 파일유형이 이미지인지 체크
      let filetype = files[i].type;
      let input = (i === 0) ? $('#thumbnail') : $('#product_images');
      if(filetype.substring(0, filetype.indexOf('/')) !== 'image') {
        fnToggleValid(input, true, ' * 이미지 파일만 첨부할 수 있습니다.');
        break;
      }
    }
    
    // 공백/초과인 입력칸이 있을 경우 submit 안함
    if($(".not-empty, #thumbnail_area, #product_images_area, .only-number").is(".is-invalid")){
      alert('아직 작성되지 않았거나 잘못된 입력사항이 있습니다.')
      return;
    }
    
    //
    let dataTransfer = new DataTransfer();
    $.each(orderedImageList, function(i, image){
      if(image.imageCode === undefined){
        dataTransfer.items.add(image);
      }
    });
    $('#product_images')[0].files = dataTransfer.files;

    // 문제 없으면 SUBMIT
    $(this).parents('form').submit();
  });
}


fnCheckInput();
fnCkeditor();
fnFileCheck();
fnAddOption();
fnRemoveOption();

fnOpenFileSelect();
fnAddImageToList();
fnDeleteImage();
fnCheckRequired();