package com.gdu.petmall.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dao.OrderMapper;
import com.gdu.petmall.dto.OrderDto;
import com.gdu.petmall.dto.UserDto;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }
    
 

    @Override
    public boolean addOrder(MultipartHttpServletRequest multipartRequest) throws Exception {
        int userNo = Integer.parseInt(multipartRequest.getParameter("userNo"));
        
        String totalPriceStr = multipartRequest.getParameter("totalPrice");
        int totalPrice = totalPriceStr != null ? Integer.parseInt(totalPriceStr) : 0; 
        
        String reName = multipartRequest.getParameter("reName");
        String reTel = multipartRequest.getParameter("reTell"); 
        String address = "ddd";
              //multipartRequest.getParameter("address");
        String addressDetail = "ccc";
        //multipartRequest.getParameter("addressDetail");
        String reText = "aaa";
        //multipartRequest.getParameter("reText");
        
        System.out.println("111111111111111111111111111111111111111");
        System.out.println(address);
        System.out.println(addressDetail);
        System.out.println(reText);
        System.out.println("111111111111111111111111111111111111111");
        
        OrderDto order = OrderDto.builder()
                .reName(reName)
                .reTel(reTel)
                .address(address)
                .addressDetail(addressDetail)
                .reText(reText)
                .totalPrice(totalPrice)
                .userDto(UserDto.builder()
                        .userNo(userNo)
                        .build())
                .build();

        orderMapper.insertOrderPay(order);
        
        return true;
    }
}