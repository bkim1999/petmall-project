package com.gdu.petmall.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.ProductDto;
import com.gdu.petmall.dto.ProductImageDto;


public interface ProductService {
  public Map<String, Object> loadCategoryList();
  public Map<String, Object> loadProductList(HttpServletRequest request);
  public void loadProductInfo(HttpServletRequest request, Model model);
  public ProductImageDto loadProductThumbnail(int productNo);
  public Map<String, Object> imageUpload(MultipartHttpServletRequest multipartRequest);
  public boolean addProduct(ProductDto product, MultipartHttpServletRequest multipartRequest) throws Exception;
  public boolean editProduct(ProductDto product, MultipartHttpServletRequest multipartRequest) throws Exception;
  public void removeProduct(int productNo, RedirectAttributes redirectAttributes);
  public void productImageBatch();
}
