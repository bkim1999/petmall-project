package com.gdu.petmall.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.gdu.petmall.dao.CartMapper;
import com.gdu.petmall.dto.CartDto;
import com.gdu.petmall.dto.ProductOptionDto;
import com.gdu.petmall.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
  
  private final CartMapper cartMapper;
  private final SqlSession sqlSession;
  
  
  
  @Override
  public void addCart(HttpServletRequest request,Model model) {
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    int optionNo = Integer.parseInt(request.getParameter("optionNo"));
    int count = Integer.parseInt(request.getParameter("count"));

    CartDto cart = CartDto.builder()
                            .userDto(UserDto.builder().userNo(userNo).build())
                            .productOptionDto(ProductOptionDto.builder().optionNo(optionNo).build())
                            .count(count)
                            .build();
       int  addResult = cartMapper.insertCart(cart);
       model.addAttribute("addResult", addResult);
    }
  
  @Override
  public void getList(HttpServletRequest request, Model model) {
    
    int userNo = 1;
    //Integer.parseInt(request.getSession().getAttribute("user").getUserNo(userNo));
    List<CartDto> cartList = cartMapper.getCartList(userNo);
    model.addAttribute("cartList", cartList);
  }
  
  @Override
  public Map<String, Object> removeCart(HttpServletRequest request) throws Exception {
    Optional<String> opt = Optional.ofNullable(request.getParameter("optionNo"));
    int optionNo = Integer.parseInt(opt.orElse(null));
    Map<String, Object> map = Map.of("optionNo", cartMapper.deleteCart(optionNo));
    int removeResult = cartMapper.deleteCart(optionNo);
    return Map.of("removeResult", removeResult);
    
  }
  
  @Override
  public void modifyCart(HttpServletRequest request, Model model) {
    
    int count = Integer.parseInt(request.getParameter("count"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    int optionNo = Integer.parseInt(request.getParameter("optionNo"));
    
    CartDto updateCart = CartDto.builder()
                                .userDto(UserDto.builder().userNo(userNo).build())
                                .productOptionDto(ProductOptionDto.builder().optionNo(optionNo).build())
                                .count(count)
                                .build();
    
    int modifyResult = cartMapper.updateCart(updateCart);
  }
  

  
  
  

 }