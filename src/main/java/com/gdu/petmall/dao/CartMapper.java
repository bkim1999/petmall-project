package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.CartDto;

@Mapper
public interface CartMapper {
  
  //카트 목록,카트 할인율
  public List<CartDto> getCartList(int userNo); 
  
  // 카트 추가
  public int insertCart(List<CartDto> addCartList);
  
  // 카트 삭제
  public int deleteCart(Map<String, Object> map);
  
  // 카트 수량 수정
  public int updateCart(Map<String, Object> map);
  
  public int deleteAllCart(List<CartDto> deleteAll);
  
}