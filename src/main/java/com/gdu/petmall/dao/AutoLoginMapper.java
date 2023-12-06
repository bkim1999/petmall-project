package com.gdu.petmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.AutoLoginDto;

@Mapper
public interface AutoLoginMapper {

	/*자동로그인 등록*/
	public int saveAutoLoginToken(AutoLoginDto autoLoginDto);
	
	/*자동로그인 조회*/
	public AutoLoginDto getAutoLoginTokenInfo(String token);
	
	/* 자동로그인 해제*/
	public int deleteAutoLoginToken(String email);
	
}
