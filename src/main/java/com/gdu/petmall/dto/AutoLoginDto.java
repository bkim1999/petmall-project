package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AutoLoginDto {

	private  String token;
	private  String lastAccess;
	private  String pw;
	private  String email; 
	private  int userNo;
	
}
