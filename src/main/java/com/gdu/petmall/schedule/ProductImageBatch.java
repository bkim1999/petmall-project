package com.gdu.petmall.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gdu.petmall.service.ProductService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductImageBatch {
    
  private final ProductService productService;
  
  @Scheduled(cron="0 2 0 1/1 * ?")
  public void execute() {
    productService.productImageBatch();
  }
 
}
