package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.ProductDto;
import com.gdu.petmall.dto.ProductImageDto;
import com.gdu.petmall.dto.ProductOptionDto;

@Mapper
public interface ProductMapper {
  public int getProductCount(Map<String, Object> map);
  public List<ProductDto> getProductList(Map<String, Object> map);
  public ProductDto getProduct(int productNo);
  public List<ProductOptionDto> getOptionList(int productNo);
  public int insertProduct (ProductDto product);
  public int insertProductOption (ProductOptionDto option);
  public List<ProductImageDto> getProductImageList(int productNo);
  public int insertProductImage(ProductImageDto productImage);
  public int deleteProduct(int productNo);
}