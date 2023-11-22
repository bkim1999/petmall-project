package com.gdu.petmall.dto;

import lombok.Data;

import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
  private int userNo;
  private String email;
  private String pw;
  private String name;
  private String gender;
  private String mobile;
  private String postcode;
  private String roadAddress;
  private String jibunAddress;
  private String detailAddress;
  private int agree;
  private int joinState;
  private String pwModifiedAt;
  private String joinedAt;
  private int adminAuthorState;
  private int point;

}
