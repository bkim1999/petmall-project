package com.gdu.petmall.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.ProfileDto;
import com.gdu.petmall.dto.UserDto;


@Mapper
public interface ProfileMapper {

	public int insertProfile(ProfileDto profile);
	public ProfileDto getProfileImage(ProfileDto profile );
	public int deleteOld(ProfileDto profile);
}
