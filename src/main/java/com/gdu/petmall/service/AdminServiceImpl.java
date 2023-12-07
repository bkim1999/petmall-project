package com.gdu.petmall.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.gdu.petmall.dao.EventMapper;
import com.gdu.petmall.dao.ProductMapper;
import com.gdu.petmall.dao.QnaMapper;
import com.gdu.petmall.dao.UserMapper;
import com.gdu.petmall.dto.EventDto;
import com.gdu.petmall.dto.QnaDto;
import com.gdu.petmall.dto.UserDto;
import com.gdu.petmall.util.MyPageUtils;
import com.gdu.petmall.util.MySecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
  
  private final ProductMapper productMapper;
  private final MyPageUtils myPageUtils;
  private final QnaMapper qnaMapper;
  private final EventMapper eventMapper;
  private final UserMapper userMapper;
  private final MySecurityUtils mySecurityUtils;
  
  @Override
  public void getQna(HttpServletRequest request, Model model) {
    
    List<QnaDto> qnaList = qnaMapper.getAllQnalist();
    
    int checkFlag = 1;
    int qnaTotalCount = qnaMapper.qnaTotalCount();
    int qnaAnswerCount = qnaMapper.getQnaCount(checkFlag);
    int qnaNonAnswerCount = qnaTotalCount-qnaAnswerCount;
    
    model.addAttribute("qnaList", qnaList);
    model.addAttribute("qnaTotalCount", qnaTotalCount);
    model.addAttribute("qnaNonAnswerCount", qnaNonAnswerCount);
    model.addAttribute("qnaAnswerCount", qnaAnswerCount);
  }
  
  @Override
  public Map<String, Object> getEvent() {
    
    List<EventDto> eventList = eventMapper.getTotalList();
    
    return Map.of("eventList",eventList);
  }
  
  @Override
  public Map<String, Object> getAjaxAlllist() {
    
    List<QnaDto> qnaList = qnaMapper.getAllQnalist();
    
    return Map.of("qnaList",qnaList);
  }
  
  @Override
  public Map<String, Object> getAjaxAnswerList(HttpServletRequest request) {
    
    int checkFlag = Integer.parseInt(request.getParameter("checkFlag"));
    
    List<QnaDto> qnaAnswerList = qnaMapper.confirmAnswer(checkFlag);
    
    return Map.of("qnaAnswerList",qnaAnswerList);
  }
  
  @Override
  public Map<String, Object> getUser() {
    
    Map<String, Object> map = new HashMap<>();
    
    List<UserDto> userList = userMapper.getUserList(map);
    
    return Map.of("userList",userList);
  }
  
  @Override
  public Map<String, Object> pwInit(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    String pw= mySecurityUtils.getSHA256("1111");
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    UserDto userDto = UserDto.builder()
                             .email(email)
                             .userNo(userNo)
                             .pw(pw)
                             .build();
    
    int pwInitResult = userMapper.changeUserInfo(userDto);
    
    
    return Map.of("pwInitResult",pwInitResult);
  }
  
  @Override
  public Map<String, Object> adminTake(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    int adminAuthorState= Integer.parseInt(request.getParameter("adminAuthorState"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    UserDto userDto = UserDto.builder()
                             .email(email)
                             .userNo(userNo)
                             .adminAuthorState(adminAuthorState)
                             .build();
    
    int adminTakeResult = userMapper.changeUserInfo(userDto);
    
    return Map.of("adminTakeResult",adminTakeResult);
  }
  
  
  
  @Override
  public Map<String, Object> normalTake(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    int adminAuthorState= Integer.parseInt(request.getParameter("adminAuthorState"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    UserDto userDto = UserDto.builder()
                            .email(email)
                            .userNo(userNo)
                            .adminAuthorState(adminAuthorState)
                            .build();

    int normalTakeResult = userMapper.changeUserInfo(userDto);
    
    return Map.of("normalTakeResult",normalTakeResult);
  }
    
  
}
