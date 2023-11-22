package com.gdu.petmall.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.InactiveUserDto;
import com.gdu.petmall.dto.LeaveUserDto;
import com.gdu.petmall.dto.UserDto;

@Mapper
public interface UserMapper {
	/*조회*/
	public UserDto getUser(Map<String, Object>map);
	public InactiveUserDto getInactiveUser(Map<String, Object> map);
	public LeaveUserDto getLeaveUser(Map<String, Object> map);
  public int getPoint (Map<String, Object>map);
	public String getEmail(Map<String, Object>map);
	public int getEmailforPw(Map<String, Object>map);
  
  /*회원삽입수정삭제*/
	public int insertUser(UserDto user);
	public int insertAccess(String email);
  public int insertLeaveUser(UserDto user);
  public int updateUser(UserDto user);
  public int deleteUser(UserDto user);
  public int updatePw(UserDto user);
  
  /*휴면 처리*/
  public int insertInactiveUser();//휴면회원목록 추가
  public int deleteUserForInactive();//회원목록에서 대상 삭제
  /*휴면 복원*/
  public int insertActiveUser(String email);//복원목록에 추가
  public int deleteInactiveUser(String email);//휴면목록에서 대상 삭제
  
  /*네이버 api 관련*/
  public int insertNaverUser(UserDto user);
  
  
 // public int updatePoint(UserDto user); // 포인트 삽입 테스트(추후에 삭제해야할것)
  
}
