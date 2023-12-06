package com.gdu.petmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.AutoLoginDto;

@Mapper
public interface AutoLoginMapper {

	public int saveAutoLoginToken(AutoLoginDto autoLoginDto);
	public AutoLoginDto getAutoLoginTokenInfo(String token);
	public int deleteAutoLoginToken(String email);
	
}
