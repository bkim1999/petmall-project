package com.gdu.petmall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@EnableWebMvc
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    
  }
  
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
    .addResourceLocations("classpath:/static/", "classpath:/templates/");
    registry.addResourceHandler("/product/**")
    .addResourceLocations("file:///product/");
    registry.addResourceHandler("/event/**")
    .addResourceLocations("file:///event/");
    registry.addResourceHandler("/user/**")
    .addResourceLocations("file:///user/"); 
  }
  
}
