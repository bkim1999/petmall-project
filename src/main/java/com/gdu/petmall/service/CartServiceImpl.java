package com.gdu.petmall.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.gdu.petmall.dao.CartMapper;
import com.gdu.petmall.dto.CartDto;
import com.gdu.petmall.dto.CartOptionListDto;
import com.gdu.petmall.dto.ProductOptionDto;
import com.gdu.petmall.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
  
  private final CartMapper cartMapper;
  
  @Override
  public void addCart(CartOptionListDto cartList, HttpServletRequest request, Model model) {
    
    List<CartDto> addCartList = new ArrayList<>(); 
    
    for(CartDto cartDto : cartList.getCartList()) {
        HttpSession session = request.getSession();
        UserDto user = (UserDto)session.getAttribute("user");
        int userNo = user.getUserNo();
      
        int optionNo = cartDto.getProductOptionDto().getOptionNo();
        int count = cartDto.getCount();
        
        CartDto cart = CartDto.builder()
            .userDto(UserDto.builder().userNo(userNo).build())
            .productOptionDto(ProductOptionDto.builder().optionNo(optionNo).build())
            .count(count)
            .build();
        
        addCartList.add(cart);
    }

    int addCartListResult = cartMapper.insertCart(addCartList);
    model.addAttribute("addCartListResult", addCartListResult);
    
    
  }
  
  @Override
  public void getList(HttpServletRequest request, Model model) {
    
    HttpSession session = request.getSession();
    UserDto user = (UserDto)session.getAttribute("user");
    int userNo = user.getUserNo();
    List<CartDto> cartList = cartMapper.getCartList(userNo);
    model.addAttribute("cartList", cartList);
    
    
  
  }
  
  @Override
  public Map<String, Object> deleteCart(HttpServletRequest request){
   
    //    HttpSession session = request.getSession();
    //    UserDto user = (UserDto)session.getAttribute("user");
    //    int userNo = user.getUserNo();
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("optionNo"));
    int optionNo = Integer.parseInt(opt.orElse("0"));
    
    Map<String, Object> map = Map.of("userNo", userNo ,
                                     "optionNo", optionNo);
        
    int removeResult = cartMapper.deleteCart(map);
    
    return Map.of("removeResult", removeResult);
  }
  
  @Override
  public Map<String, Object> minusCart(HttpServletRequest request) {
    
    int optionNo = Integer.parseInt(request.getParameter("optionNo"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    int count = Integer.parseInt(request.getParameter("count"));
    
    count = count - 1;
    
    Map<String, Object> map = Map.of("userNo", userNo ,
                                    "optionNo", optionNo,
                                       "count", count);
    
    int minusResult = cartMapper.updateCart(map);
    
    return Map.of("minusResult", minusResult);
  }
  
  @Override
  public Map<String, Object> plusCart(HttpServletRequest request) {
    
    int optionNo = Integer.parseInt(request.getParameter("optionNo"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    int count = Integer.parseInt(request.getParameter("count"));
    
    count = count + 1;
    
    Map<String, Object> map = Map.of("userNo", userNo ,
                                    "optionNo", optionNo,
                                       "count", count);
    
    int plusResult = cartMapper.updateCart(map);
    
    return Map.of("plusResult", plusResult);
  }

  


}
