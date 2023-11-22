package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class LeaveUserDto {
  private String email;
  private String joinedAt;
  private String leavedAt;
}
