package com.gdu.petmall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gdu.petmall.dao.AutoLoginMapper;
import com.gdu.petmall.dao.UserMapper;
import com.gdu.petmall.interceptor.AdminAuthRequiredInterceptor;
import com.gdu.petmall.interceptor.AutoLoginInterceptor;
import com.gdu.petmall.interceptor.RequiredLoginInterceptor;
import com.gdu.petmall.interceptor.ShouldNotLoginInterceptor;

import lombok.RequiredArgsConstructor;

@EnableWebMvc
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  
	
  private final RequiredLoginInterceptor requiredLoginInterceptor;
  private final ShouldNotLoginInterceptor shouldNotLoginInterceptor;
  private final AutoLoginMapper autoLoginMapper;
  private final UserMapper userMapper;

@Override
  public void addInterceptors(InterceptorRegistry registry) {
	
	
	  registry.addInterceptor(new AutoLoginInterceptor(autoLoginMapper, userMapper))
      .addPathPatterns("/**") 
      .excludePathPatterns(); 
	  
	  
	 
	  registry.addInterceptor(new ShouldNotLoginInterceptor())
  	  .addPathPatterns("/user/join.form"
  	                 , "/login.form"
  	                 , "/user/join_option.form"
  	                 , "/user/find_id.form"
  	                 , "/user/change_pw.form");
	  
	 
    registry.addInterceptor(new RequiredLoginInterceptor())
  	  .addPathPatterns("/mypage")
  	  .addPathPatterns("/review/list.do"
  	                 , "/review/addReview.form"
  	                 , "/review/addReview.do"
  	                 , "/review/editReview.form"
  	                 , "/review/editReview.do"
  	                 , "/review/removeReview.do");
    
    registry.addInterceptor(new AdminAuthRequiredInterceptor())
    .addPathPatterns("/product/addProduct.form"
                   , "/product/addProduct.do"
                   , "/product/editProduct.form"
                   , "/product/editProduct.do"
                   , "/product/removeProduct.do");
    
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
