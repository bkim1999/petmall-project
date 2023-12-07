package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.InactiveUserDto;
import com.gdu.petmall.dto.LeaveUserDto;
import com.gdu.petmall.dto.UserDto;

@Mapper
public interface UserMapper {
	public UserDto getUser(Map<String, Object>map);
	public InactiveUserDto getInactiveUser(Map<String, Object> map);
	public LeaveUserDto getLeaveUser(Map<String, Object> map);
  public int getPoint (Map<String, Object>map);
	public String getEmail(Map<String, Object>map);
	public int getEmailforPw(Map<String, Object>map);
	public int getEmailResult(String email);
	public int getEmailResultInactive(String email);
	public List<UserDto>getUserList(Map<String, Object>user);
  
	public int insertUser(UserDto user);
	public int insertAccess(String email);
  public int insertLeaveUser(UserDto user);
  public int updateUser(UserDto user);
  public int deleteUser(UserDto user);
  public int updatePw(UserDto user);
  
  public int insertInactiveUser();
  public int deleteUserForInactive();
 
  public int insertActiveUser(String email);
  public int deleteInactiveUser(String email);
  

  public int insertNaverUser(UserDto user);
  
 
  public int insertKakaoUser(UserDto user);
  
  
  //관리자 
  public int changeUserInfo(UserDto userDto);
  
}
