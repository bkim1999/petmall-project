// QnaDto class
package com.gdu.petmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QnaDto {
    private int qnaNo;
    private UserDto userDto; 
    private int productNo;
    private int title; 
    private String contents;
    private String createdAt;
    private int checkFlag;
    private String textPw;
    private int status;
    private int depth;
    private int groupNo;
    private int userNo;
}
