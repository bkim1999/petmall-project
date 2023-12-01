package com.gdu.petmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.ProfileDto;
import com.gdu.petmall.dto.UserDto;


@Mapper
public interface ProfileMapper {

	public int insertProfile(ProfileDto profile);// 프로필 첨부
	public ProfileDto getProfileImage(ProfileDto profile ); // 첨부한 이미지 가져옴
	public int deleteOld( );
}
