package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartDto {

  private int count;
  private ProductOptionDto productOptionDto;
  private ProductDto productDto;
  private UserDto userDto;
  private EventDto eventDto;
} 
