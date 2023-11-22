package com.gdu.petmall.util;

import java.security.MessageDigest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class MySecurityUtils {
  
  // SHA256 암호화
  
  public String getSHA256(String password) {
    
    StringBuilder sb = new StringBuilder();
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      messageDigest.update(password.getBytes());
      byte[] b = messageDigest.digest();
      for(int i = 0; i < b.length; i++) {
        sb.append(String.format("%02X",  b[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return sb.toString();
    
  }
  
  public String getRandomString(int count, boolean letters, boolean numbers) {
    return RandomStringUtils.random(count, letters, numbers);
  }
  
  public String preventXSS(String source) {
    return source.replace("<", "&lt").replace(">", "&gt");
  }
  
}
