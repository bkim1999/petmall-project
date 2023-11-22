package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EventImageDto {
  
  private int eventNo;
  private String path;
  private String originalFilename;
  private String FilesystemName;
  
}
