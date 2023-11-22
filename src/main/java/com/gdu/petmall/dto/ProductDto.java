package com.gdu.petmall.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDto {
  private int productNo;
	private String productName;
	private String productTitle;
	private String productContents;
	private int productPrice;
	private String productDescription;
	private String productSize;
	private String productWarning;
	private int categoryNo;
	private int productHit;
	private double productRating;
	private int productCount;                          // 목록보기용 (PRODUCT_T에는 없음)
	private int reviewCount;                           // 목록보기용 (PRODUCT_T에는 없음)
	private ProductOptionListDto productOptionList;    // 목록보기용 (PRODUCT_T에는 없음)
	private ProductImageDto productImageDto;           // 목록보기용 (PRODUCT_T에는 없음)
}
