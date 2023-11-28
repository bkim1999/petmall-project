package com.gdu.petmall.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dao.ProductMapper;
import com.gdu.petmall.dto.CategoryDto;
import com.gdu.petmall.dto.ProductDto;
import com.gdu.petmall.dto.ProductImageDto;
import com.gdu.petmall.dto.ProductOptionDto;
import com.gdu.petmall.util.MyFileUtils;
import com.gdu.petmall.util.MyPageUtils;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
  
  private final ProductMapper productMapper;
  private final MyPageUtils myPageUtils;
  private final MyFileUtils myFileUtils;
  
  @Override
  public Map<String, Object> loadCategoryList() {
    // 카테고리 목록 DB에 요청
    List<CategoryDto> categoryList = productMapper.getCategoryList(); 
    return Map.of("categoryList", categoryList);
  }
  
  @Override
  public Map<String, Object> loadProductList(HttpServletRequest request) {
    
    // 카테고리 번호 NULL 체크
    Optional<String> opt = Optional.ofNullable(request.getParameter("categoryNo"));
    int categoryNo = Integer.parseInt(opt.orElse("0"));
    
    // 검색어 구하기
    String searchText = request.getParameter("searchText");
    
    // 해당 카테고리 상품 개수 DB에 요청, 0일시 메세지 반환
    Map<String, Object> map = new HashMap<>();
    map.put("categoryNo", categoryNo);
    map.put("searchText", searchText);
    int productCount = productMapper.getProductCount(map);
    if(productCount == 0) {
      map = new HashMap<>();
      map.put("productList", null);
      map.put("message", "아직 상품이 준비되지 않았습니다.");
      return map;
    }
    
    // 상품 개수로 페이지 생성
    opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    int display = 10;
    myPageUtils.setPaging(page, productCount, display);
    int begin = myPageUtils.getBegin();
    int end = myPageUtils.getEnd();
    
    // 순서 기준 NULL 체크
    opt = Optional.ofNullable(request.getParameter("order"));
    String order = opt.orElse("PRODUCT_SALES");
    
    // 카테고리번호, 순서, 페이지 시작/끝 map에 저장
    map = Map.of("categoryNo", categoryNo
                                   , "order", order
                                   , "begin", begin
                                   , "end", end);
    
    // 상품 목록 DB에 요청(map 전달)
    List<ProductDto> productList = productMapper.getProductList(map);
    // Map에 담아 반환(상품 목록, 총 페이지수) 
    return Map.of("productList", productList
                , "totalPage", myPageUtils.getTotalPage());
    
  }
  
  @Override
  public void loadProductInfo(HttpServletRequest request, Model model) {
    int productNo = Integer.parseInt(request.getParameter("productNo"));
    ProductDto product = productMapper.getProduct(productNo);
    List<ProductOptionDto> optionList = productMapper.getOptionList(productNo);
    
    model.addAttribute("product", product);
    model.addAttribute("optionList", optionList);
  }
  
  @Override
  public Map<String, Object> imageUpload(MultipartHttpServletRequest multipartRequest) {
    
    // 이미지가 저장될 경로
    String imagePath = myFileUtils.getProductImagePath();
    File dir = new File(imagePath);
    if(!dir.exists()) {
      dir.mkdirs();
    }
    
    // 이미지 파일 (CKEditor는 이미지를 upload라는 이름으로 보냄)
    MultipartFile upload = multipartRequest.getFile("upload");
    
    // 이미지가 저장될 이름
    String originalFilename = upload.getOriginalFilename();
    String filesystemName = myFileUtils.getFilesystemName(originalFilename);
    
    // 이미지 File 객체
    File file = new File(dir, filesystemName);
    
    // 저장
    try {
      upload.transferTo(file);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // CKEditor로 저장된 이미지의 경로를 JSON 형식으로 반환해야 함
    return Map.of("uploaded", true
                , "url", multipartRequest.getContextPath() + imagePath + "/" + filesystemName);
    
    // url: "http://localhost:8080/product/2023/11/24/filesystemName.jpg"
    
  }
  
  public List<String> getEditorImageList(String contents) {
    
    // Editor에 추가한 이미지 목록 반환
    
    List<String> editorImageList = new ArrayList<>();
    
    Document document = Jsoup.parse(contents);
    Elements elements =  document.getElementsByTag("img");
    
    if(elements != null) {
      for(Element element : elements) {
        String src = element.attr("src");
        String filesystemName = src.substring(src.lastIndexOf("/") + 1);
        editorImageList.add(filesystemName);
      }
    }
    
    return editorImageList;
    
  }
  
  @Transactional
  @Override
  public boolean addProduct(ProductDto product, MultipartHttpServletRequest multipartRequest) throws Exception {
    
    // 모든 제품, 옵션, 사진 정보가 DB에 저장됬었는지 boolean으로 확인
    boolean addProductResult;
    boolean addOptionResult;
    boolean addThumbnailResult;
    boolean addContentsImageResult;
    boolean addDisplayImageResult;

    // 상품 관련 사진 저장할 경로 생성
    String imagePath = myFileUtils.getProductImagePath();
    
    /////////////////////////////////
    /////////상품 정보 DB에 저장/////////
    ////////////////////////////////
    addProductResult = productMapper.insertProduct(product) == 1;
    
    /////////////////////////////////
    ///////상품 옵션 정보 DB에 저장///////
    ////////////////////////////////
    
    int optionInsertCount = 0; // 모든 옵션이 DB에 추가되었는지 확인하는 카운터
    int productNo = product.getProductNo(); // 등록한 상품번호
    List<ProductOptionDto> productOptionList = product.getProductOptionList(); // 등록할 옵션 목록
    int optionListSize = 0;

    // 옵션이 없을 경우
    if(productOptionList == null) {
      ProductOptionDto option = ProductOptionDto.builder()
                                                .productNo(productNo)
                                                .optionName("기본")
                                                .build();
      optionInsertCount += productMapper.insertProductOption(option);
      optionListSize = 1;
    } else {  // 옵션이 있을 경우
      for(ProductOptionDto option : product.getProductOptionList()) {
        optionListSize = productOptionList.size();
        option.setProductNo(productNo);
        optionInsertCount += productMapper.insertProductOption(option);
      }
    }
    
    // 추가된 옵션의 수가 모든 옵션의 수와 같을 시 true
    addOptionResult = optionListSize == optionInsertCount;
    
    
    /////////////////////////////////
    ////상품 썸네일 사진 정보 DB에 저장/////
    ////////////////////////////////
    
    int insertThumbnailCount = 0;
    
    // multipartRequest에서 파일 get
    MultipartFile thumbnailMulti = multipartRequest.getFile("thumbnail");
    
    // multipartFile -> File 로 변환 후 imagePath 경로에 저장
    String thumbnailName = myFileUtils.getFilesystemName(thumbnailMulti.getOriginalFilename());
    File thumbnailOriginalFile = new File(imagePath, thumbnailName);
    thumbnailMulti.transferTo(thumbnailOriginalFile);
    
    // 크기조절한 썸네일 사진이 저장될 File 생성
    File previewFile = new File(imagePath, "p_" + thumbnailName);
    File thumbnailFile = new File(imagePath, "t_" + thumbnailName);
    
    // Thumbnailator로 크기조절 후 저장
    Thumbnails.of(thumbnailOriginalFile)
              .size(100, 100)      // 가로 100px, 세로 100px
              .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
              .toFile(previewFile);
    Thumbnails.of(thumbnailOriginalFile)
              .size(450, 450)      // 가로 450px, 세로 450px
              .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
              .toFile(thumbnailFile);
    ProductImageDto previewImage = ProductImageDto.builder()
                                                    .imageCode("product_" + productNo)
                                                    .position("preview")
                                                    .path(imagePath)
                                                    .filesystemName("p_" + thumbnailName)
                                                    .build();
    ProductImageDto thumbnailImage = ProductImageDto.builder()
                                                  .imageCode("product_" + productNo)
                                                  .position("thumbnail")
                                                  .path(imagePath)
                                                  .filesystemName("t_" + thumbnailName)
                                                  .build();
    // DB로 저장
    insertThumbnailCount += productMapper.insertProductImage(previewImage);
    insertThumbnailCount += productMapper.insertProductImage(thumbnailImage);
    
    addThumbnailResult = insertThumbnailCount == 2;
    
    /////////////////////////////////
    ///////상품 사진 정보 DB에 저장///////
    ////////////////////////////////
    
    int insertDisplayImageCount;
    
    // multipartRequest에서 파일 get
    List<MultipartFile> productImages = multipartRequest.getFiles("product_images");
    if(productImages.get(0).getSize() == 0) { // 상품 사진이 없을 때
      insertDisplayImageCount = 1;
    } else {
      insertDisplayImageCount = 0;
    }
    
    // 각 파일 File로 변환 -> 크기 조절 -> 정보 DB로 저장
    for(MultipartFile productImage : productImages) {
      if(productImage != null && !productImage.isEmpty()) { // 사진이 null이 아니거나 비어있지 않을 때
        
        // 변환: MultipartFile -> File
        String productImageFilename = myFileUtils.getFilesystemName(productImage.getOriginalFilename());
        File originalProductImageFile = new File(imagePath, productImageFilename);
        productImage.transferTo(originalProductImageFile);
        
        // 크기 조절
        File productImageFile = new File(imagePath, "d_" + productImageFilename);
        Thumbnails.of(originalProductImageFile)
                  .size(754, 754)      // 가로 754px, 세로 754px
                  .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
                  .toFile(productImageFile);
        
        // DB로 저장
        ProductImageDto productImageDto = ProductImageDto.builder()
                                                         .imageCode("product_" + productNo)
                                                         .position("display")
                                                         .path(imagePath)
                                                         .filesystemName("d_" + productImageFilename)
                                                         .build();
        // 카운터 증가
        insertDisplayImageCount += productMapper.insertProductImage(productImageDto);
        
      }
      
    }
    
    addDisplayImageResult = productImages.size() == insertDisplayImageCount;
    
    /////////////////////////////////
    ////상품 설명 사진 정보 DB에 저장///////
    ////////////////////////////////
    
    int insertContentsImageCount = 0;
    
    // DB에 저장
    for(String editorImage : getEditorImageList(product.getProductContents())) {
    ProductImageDto productImageDto = ProductImageDto.builder()
              .imageCode("product_" + productNo)
              .position("contents")
              .path(imagePath)
              .filesystemName(editorImage)
              .build();
      insertContentsImageCount += productMapper.insertProductImage(productImageDto);
    }
    
    addContentsImageResult = getEditorImageList(product.getProductContents()).size() == insertContentsImageCount;
    
    // 상품, 옵션, 사진 정보 삽입 결과 반환
    return addProductResult && addOptionResult && addThumbnailResult && addDisplayImageResult && addContentsImageResult;
      
  }
  
  @Override
  public Map<String, Object> loadProductImageList(HttpServletRequest request) {
    int productNo = Integer.parseInt(request.getParameter("productNo"));
    return Map.of("productImageList", productMapper.getProductImageList(productNo));
  }
  
  @Override
  public void removeProduct(int productNo, RedirectAttributes redirectAttributes) {
    int removeProductResult = productMapper.deleteProduct(productNo);
    redirectAttributes.addFlashAttribute("removeProductResult", removeProductResult);
  }
  
}
