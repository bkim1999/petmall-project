package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QattachDto {
	  private int qattachNo;
	  private int qnaNo;
	  private String path;
	  private String originalFilename;
	  private String filesystemName;
}