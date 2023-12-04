package com.gdu.petmall.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.petmall.dao.OrderMapper;
import com.gdu.petmall.dao.PayMapper;
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
        String postcode = multipartRequest.getParameter("postcode");
        String roadAddress = multipartRequest.getParameter("roadAddress");
        String jibunAddress = multipartRequest.getParameter("jibunAddress");
        String detailAddress =multipartRequest.getParameter("detailAddress");
        String reText =multipartRequest.getParameter("reText");
        String yourOrder = multipartRequest.getParameter("yourOrder");
        
        
        OrderDto order = OrderDto.builder()
                .reName(reName)
                .reTel(reTel)
                .postcode(postcode)
                .roadAddress(roadAddress)
                .jibunAddress(jibunAddress)
                .detailAddress(detailAddress)
                .reText(reText)
                .totalPrice(totalPrice)
                .yourOrder(yourOrder)
                .userDto(UserDto.builder()
                        .userNo(userNo)
                        .build())
                .build();

        orderMapper.insertOrderPay(order);
        
        return true;
    }
    
    
    @Override
    public int getLoggedInUserNo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null) {
            UserDto loggedInUser = (UserDto) session.getAttribute("user");
            return loggedInUser.getUserNo();
        } else {
            return -1;
        }
    }
    
    @Override
    public Map<String, Object> myOrderList(HttpServletRequest request) {
        int userNo = getLoggedInUserNo(request); 

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userNo", userNo);

        List<OrderDto> myOrderList = orderMapper.getMyOrderList(paramMap);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("myOrderList", myOrderList);

        return resultMap;
    }
    

    @Override
    public List<OrderDto> getOrder(int totalPrice,String yourOrder ) {
       OrderDto orderDto =OrderDto.builder()
                            .yourOrder(yourOrder)
                            .totalPrice(totalPrice)
                            .build();
        return orderMapper.getOrder(orderDto);
    }


}
    