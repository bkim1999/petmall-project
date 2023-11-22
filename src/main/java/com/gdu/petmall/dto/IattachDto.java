package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IattachDto {
  private int iattachNo;
  private int inquiryNo;
  private String path;
  private String originalFilename;
  private String filesystemName;
}
