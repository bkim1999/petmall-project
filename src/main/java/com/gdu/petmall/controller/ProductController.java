package com.gdu.petmall.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dto.ProductDto;
import com.gdu.petmall.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/product")
@RequiredArgsConstructor
@Controller
public class ProductController {
  
  private final ProductService productService;
  
  @GetMapping(value="/list.do")
  public String list() {
    return "/product/list";
  }
  
  @ResponseBody
  @GetMapping(value="/getCategoryList.do", produces="application/json")
  public Map<String, Object> loadCategoryList() {
    return productService.loadCategoryList();
  }
  
  @ResponseBody
  @GetMapping(value="/getProductList.do", produces="application/json")
  public Map<String, Object> loadProductList(HttpServletRequest request) {
    return productService.loadProductList(request);
  }
  
  @GetMapping(value="/detail.do")
  public String detail(HttpServletRequest request, Model model) {
    productService.loadProductInfo(request, model);
    return "/product/detail";
  }
  
  @ResponseBody
  @PostMapping(value="/imageUpload.do")
  public Map<String, Object> imageUpload(MultipartHttpServletRequest multipartRequest) {
    return productService.imageUpload(multipartRequest);
  }
  
  @GetMapping(value="/addProduct.form")
  public String addProductForm(Model model) {
    Map<String, Object> map = productService.loadCategoryList();
    model.addAttribute("categoryList", map.get("categoryList"));
    return "/product/add_product";
  }
  
  @PostMapping(value="/addProduct.do")
  public String addProduct(@ModelAttribute ProductDto product
                          , MultipartHttpServletRequest multipartrequest
                          , RedirectAttributes redirectAttributes) throws Exception {
    boolean addProductResult = productService.addProduct(product, multipartrequest);
    redirectAttributes.addFlashAttribute("addProductResult", addProductResult);
    return "redirect:/product/list.do";
  }
  
  @GetMapping(value="/editProduct.form")
  public String editProductForm(HttpServletRequest request, Model model) {
    Map<String, Object> map = productService.loadCategoryList();
    model.addAttribute("categoryList", map.get("categoryList"));
    productService.loadProductInfo(request, model);
    return "/product/edit_product";
  }
  
  @PostMapping(value="/editProduct.do")
  public String editProduct(@ModelAttribute ProductDto product
                          , MultipartHttpServletRequest multipartrequest
                          , RedirectAttributes redirectAttributes) throws Exception {
    boolean editProductResult = productService.editProduct(product, multipartrequest);
    redirectAttributes.addFlashAttribute("editProductResult", editProductResult);
    return "redirect:/product/list.do";
  }
  
  @PostMapping(value="/removeProduct.do")
  public String removeProduct(@RequestParam(value="productNo") int productNo, RedirectAttributes redirectAttributes) {
    productService.removeProduct(productNo, redirectAttributes);
    return "redirect:/product/list.do";
  }
  
}
