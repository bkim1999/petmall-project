package com.gdu.petmall.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.ProductDto;
import com.gdu.petmall.dto.ProductOptionDto;


public interface ProductService {
  public Map<String, Object> loadProductList(HttpServletRequest request);
  public void loadProductInfo(HttpServletRequest request, Model model);
  public Map<String, Object> imageUpload(MultipartHttpServletRequest multipartRequest);
  public boolean addProduct(ProductDto product, List<ProductOptionDto> productOptionList, MultipartHttpServletRequest multipartRequest) throws Exception;
  public Map<String, Object> loadProductImageList(HttpServletRequest request);
  public void removeProduct(int productNo, RedirectAttributes redirectAttributes);
}
