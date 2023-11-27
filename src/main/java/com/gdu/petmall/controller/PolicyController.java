package com.gdu.petmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policy")
public class PolicyController {
  
  @GetMapping(value="agreement.do")
  public String agreement() {
    return "policy/agreement";
  }
  @GetMapping(value="privacy.do")
  public String privacy() {
    return "policy/privacy";
  }
  @GetMapping(value="return.do")
  public String productReturn() {
    return "policy/return";
  }
  @GetMapping(value="shopinfo.do")
  public String shopinfo() {
    return "policy/shopinfo";
  }

}
