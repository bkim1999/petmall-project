package com.gdu.petmall.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
  public Map<String, Object> loadProductList(HttpServletRequest request) {
    
    // 카테고리 목록 DB에 요청
    List<CategoryDto> categoryList = productMapper.getCategoryList(); 
    
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
      map.put("categoryList", categoryList);
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
    
    // 카테고리, 순서, 페이지 시작/끝 map에 저장
    map = Map.of("categoryNo", categoryNo
                                   , "order", order
                                   , "begin", begin
                                   , "end", end);
    
    // 상품 목록 DB에 요청(map 전달)
    List<ProductDto> productList = productMapper.getProductList(map);
    // Map에 담아 반환(카테고리 목록, 상품 목록, 총 페이지수) 
    return Map.of("categoryList", categoryList
                , "productList", productList
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
    LocalDate today = LocalDate.now();
    String imagePath = "/product/" + DateTimeFormatter.ofPattern("yyyy/MM/dd").format(today);
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
    
    // url: "http://localhost:8080/petmall/product/1/main/filesystemName.jpg"
    // sevlet-context.xml에
    // <resources /product/** -> /product/
    
  }
  
  public List<String> getEditorImageList(String contents) {
    
    //** 신규 메소드 **//
    // Editor에 추가한 이미지 목록 반환하기 (Jsoup 라이브러리 사용)
    
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
  public boolean addProduct(ProductDto product, List<ProductOptionDto> productOptionList, MultipartHttpServletRequest multipartRequest) throws Exception {
    
    // Add ProductDto
    LocalDate today = LocalDate.now();
    String imagePath = "/product/" + DateTimeFormatter.ofPattern("yyyy/MM/dd").format(today);
    int addProductResult = productMapper.insertProduct(product);
    
    // Add ProductOptionDtos
    for(ProductOptionDto option : productOptionList) {
      option.setProductNo(product.getProductNo());
      productMapper.insertProductOption(option);
    }
    
    
    // Add ProductImageDtos
    for(String editorImage : getEditorImageList(product.getProductContents())) {
      ProductImageDto productImage = ProductImageDto.builder()
                                      .imageCode("product_" + product.getProductNo())
                                      .position("contents")
                                      .path(imagePath)
                                      .filesystemName(editorImage)
                                      .build();
      productMapper.insertProductImage(productImage);
    }
    
    // Add ProductImageDto(Product thumnail)
    MultipartFile thumbnail = multipartRequest.getFile("thumbnail");
    String filesystemName = myFileUtils.getFilesystemName(thumbnail.getOriginalFilename());
    File file = new File(imagePath, filesystemName);
    File thumbnailFile = new File(imagePath, filesystemName);
    thumbnail.transferTo(file);
    
    Thumbnails.of(file)
              .size(400, 400)      // 가로 400px, 세로 400px
              .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
              .toFile(thumbnailFile);
    ProductImageDto thumbnailImage = ProductImageDto.builder()
        .imageCode("product_" + product.getProductNo())
        .position("thumbnail")
        .path(imagePath)
        .filesystemName(filesystemName)
        .build();
    productMapper.insertProductImage(thumbnailImage);
    
    // Add ProductImageDto(Product images)
    List<MultipartFile> productImages = multipartRequest.getFiles("product_images");
    int attachCount;
    if(productImages.get(0).getSize() == 0) {
      attachCount = 1;
    } else {
      attachCount = 0;
    }
    
    for(MultipartFile productImage : productImages) {
      
      if(productImage != null && !productImage.isEmpty()) {
        
        File dir = new File(imagePath);
        String productImageFilename = myFileUtils.getFilesystemName(productImage.getOriginalFilename());
        File tempFile = new File(dir, productImageFilename);
        productImage.transferTo(tempFile);

        File productImageFile = new File(dir, "d_" + productImageFilename);
        Thumbnails.of(tempFile)
                  .size(754, 754)      // 가로 754px, 세로 754px
                  .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE)
                  .toFile(productImageFile);

        ProductImageDto productImageDto = ProductImageDto.builder()
            .imageCode("product_" + product.getProductNo())
            .position("display")
            .path(imagePath)
            .filesystemName(productImageFilename)
            .build();
        attachCount += productMapper.insertProductImage(productImageDto);
        
      }
      
    }
    
    return (addProductResult == 1) && (productImages.size() == attachCount);
      
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
