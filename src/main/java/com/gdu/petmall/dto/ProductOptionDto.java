package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductOptionDto {
  private int optionNo;
  private int productNo;
  private String optionName;
  private int addPrice;
  private int optionCount;
}
