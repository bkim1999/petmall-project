package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FaqDto {

  private int faqNo;
  private String faqName;
  private String faqTitle;
  private String faqContents;
  private int status;
  private int depth;
  private int groupNo;
  private int groupOrder;
  private FaqCategoryDto faqCategoryDto;
}
