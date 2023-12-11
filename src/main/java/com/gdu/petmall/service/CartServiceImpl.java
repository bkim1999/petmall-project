package com.gdu.petmall.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
  public void addCart(CartOptionListDto cartList, Model model) {
    
    List<CartDto> addCartList = new ArrayList<>(); 
    
    for(CartDto cartDto : cartList.getCartList()) {
        int userNo = cartDto.getUserDto().getUserNo();
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
   
    HttpSession session = request.getSession();
    UserDto user = (UserDto)session.getAttribute("user");
    int userNo = user.getUserNo();
    int optionNo = Integer.parseInt(request.getParameter("optionNo"));
    
    
    Map<String, Object> map = new HashMap<>();
    map.put("userNo", userNo);
    map.put("optionNo", optionNo);
             
    int removeResult = cartMapper.deleteCart(map);
    
    return Map.of("removeResult", removeResult);
  }
  
  @Override
  public Map<String, Object> minusCart(HttpServletRequest request) {
    
    HttpSession session = request.getSession();
    UserDto user = (UserDto)session.getAttribute("user");
    int userNo = user.getUserNo();
    int optionNo = Integer.parseInt(request.getParameter("optionNo"));
    int count = Integer.parseInt(request.getParameter("count"));
    int productNo = Integer.parseInt(request.getParameter("productNo"));
    String optionName = request.getParameter("optionName");
    
    System.out.println("count before update: " + count);
    
    count = count - 1;
    
    System.out.println("count after update: " + count);
    
    Map<String, Object> map = new HashMap<>();
    map.put("userNo", userNo);
    map.put("optionNo", optionNo);
    map.put("count", count);
    map.put("productNo", productNo);
    map.put("optionName", optionName);
    
    int minusResult = cartMapper.updateCart(map);
    System.out.println("minus:" + map);
    return Map.of("minusResult", minusResult);
  }
  
  @Override
  public Map<String, Object> plusCart(HttpServletRequest request) {
    
    
    HttpSession session = request.getSession();
    UserDto user = (UserDto)session.getAttribute("user");
    int userNo = user.getUserNo();
    int optionNo = Integer.parseInt(request.getParameter("optionNo"));
    int count = Integer.parseInt(request.getParameter("count"));
    int productNo = Integer.parseInt(request.getParameter("productNo"));
    String optionName = request.getParameter("optionName");
    
    
    System.out.println("count before update: " + count);

    count = count + 1;

    System.out.println("count after update: " + count);
    
    
    Map<String, Object> map = new HashMap<>();
    map.put("optionNo", optionNo);
    map.put("userNo", userNo);
    map.put("count", count);
    map.put("productNo", productNo);
    map.put("optionName", optionName);
   
    int plusResult = cartMapper.updateCart(map);
    
    
    return Map.of("plusResult", plusResult);
  }

  @Override
  public Map<String, Object> deleteAllCart(CartOptionListDto cartList, HttpServletRequest request) {
    
    List<CartDto> deleteAll = new ArrayList<>();
    
    for(CartDto cartDto : cartList.getCartList()) {
      int userNo = cartDto.getUserDto().getUserNo();
      int optionNo = cartDto.getProductOptionDto().getOptionNo();
      int count = cartDto.getCount();
      
          CartDto cart = CartDto.builder()
                                .userDto(UserDto.builder().userNo(userNo).build())
                                .productOptionDto(ProductOptionDto.builder().optionNo(optionNo).build())
                                .count(count)
                                .build();
    
          deleteAll.add(cart);
          Map<String, Object> map = new HashMap<>();
          map.put("userNo", userNo);
          map.put("optionNo", optionNo);
      }
    
    
    int daleteAllCartListResult = cartMapper.deleteAllCart(deleteAll);
   
   
    
   return Map.of ("daleteAllCartListResult", daleteAllCartListResult);
    
  }
    
    
}
