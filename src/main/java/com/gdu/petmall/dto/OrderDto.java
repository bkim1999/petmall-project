package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDto {
  private int orderNo;
  private UserDto userDto;  
  private int orderState;
  private String deliveryDivision;
  private int deliveryCost;
  private int totalPrice;
  private String orderDate;
  private int point;
  private int payment;
  private String reName;
  private String zipcode;
  private String address;
  private String addressDetail;
  private String reTel;
  private String reText;
}