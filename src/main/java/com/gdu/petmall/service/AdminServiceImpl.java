package com.gdu.petmall.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.gdu.petmall.dao.EventMapper;
import com.gdu.petmall.dao.ProductMapper;
import com.gdu.petmall.dao.QnaMapper;
import com.gdu.petmall.dao.UserMapper;
import com.gdu.petmall.dto.EventDto;
import com.gdu.petmall.dto.InactiveUserDto;
import com.gdu.petmall.dto.LeaveUserDto;
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
    
    int accessUserCount = userMapper.accessUserCount();
    int unAccessUserCount = userMapper.unAccessUserCount();
    int leaveUserCount = userMapper.leaveUserCount();
    
    int totalUserCount = accessUserCount+unAccessUserCount;
    
    
    
    return Map.of("userList",userList
                  ,"totalUserCount",totalUserCount
                  ,"accessUserCount",accessUserCount
                  ,"unAccessUserCount",unAccessUserCount
                  ,"leaveUserCount",leaveUserCount);
  }
  
  @Override
  public Map<String, Object> pwInit(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    String pw= mySecurityUtils.getSHA256("1111");
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    int agree =3;
    int adminState =3;
    
    UserDto userDto = UserDto.builder()
                             .email(email)
                             .userNo(userNo)
                             .pw(pw)
                             .agree(agree)
                             .adminAuthorState(adminState)
                             .build();
    
    int pwInitResult = userMapper.changeUserInfo(userDto);
    
    
    return Map.of("pwInitResult",pwInitResult);
  }
  
  @Override
  public Map<String, Object> adminTake(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    int adminAuthorState= Integer.parseInt(request.getParameter("adminAuthorState"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    int agree = 3;
    
    UserDto userDto = UserDto.builder()
                             .email(email)
                             .userNo(userNo)
                             .adminAuthorState(adminAuthorState)
                             .agree(agree)
                             .build();
    
    int adminTakeResult = userMapper.changeUserInfo(userDto);
    
    
    return Map.of("adminTakeResult",adminTakeResult);
  }
  
  
  
  @Override
  public Map<String, Object> normalTake(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    int adminAuthorState= Integer.parseInt(request.getParameter("adminAuthorState"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    int agree = 3;
    
    UserDto userDto = UserDto.builder()
                            .adminAuthorState(adminAuthorState)
                            .userNo(userNo)
                            .email(email)
                            .agree(agree)
                            .build();
    
    
    int normalTakeResult = userMapper.changeUserInfo(userDto);
    
    
    return Map.of("normalTakeResult",normalTakeResult);
  }
  
  public Map<String, Object> changeagree(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    int changeagree= Integer.parseInt(request.getParameter("changeagree"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    int adminAuthorState =3;
    
    UserDto userDto = UserDto.builder()
                  .email(email)
                  .userNo(userNo)
                  .agree(changeagree)
                  .adminAuthorState(adminAuthorState)
                  .build();
    
    int changeagreeResult = userMapper.changeUserInfo(userDto);
    
    return Map.of("changeagreeResult",changeagreeResult);
  }
  
  public Map<String, Object> changedeagree(HttpServletRequest request) {
    
    String email = request.getParameter("email");
    int changedeagree= Integer.parseInt(request.getParameter("changedeagree"));
    int userNo = Integer.parseInt(request.getParameter("userNo"));
    
    int adminAuthorState = 3;
    
    UserDto userDto = UserDto.builder()
        .email(email)
        .userNo(userNo)
        .agree(changedeagree)
        .adminAuthorState(adminAuthorState)
        .build();
    
    int changedeagreeResult = userMapper.changeUserInfo(userDto);
    
    return Map.of("changedeagreeResult",changedeagreeResult);
  }
  
  @Override
  public Map<String, Object> searchInfo(HttpServletRequest request) {
    
    String column = request.getParameter("column");
    String query = request.getParameter("query");
   
    Map<String, Object> map = new HashMap<>();
    
    map.put("column", column);
    map.put("query",query);
    
    List<UserDto> userList = userMapper.getUserList(map);
    
    return Map.of("userList",userList);
  }
  
  @Override
  public Map<String, Object> LeaveSearchInfo(HttpServletRequest request) {
    String column = request.getParameter("column");
    String query = request.getParameter("query");
   
    Map<String, Object> map = new HashMap<>();
    
    map.put("column", column);
    map.put("query",query);
    
    List<LeaveUserDto> userList = userMapper.LeaveAccessUserList(map);
    
    return Map.of("userList",userList);
  }
  
  @Override
  public Map<String, Object> UnaccessSearchInfo(HttpServletRequest request) {
    String column = request.getParameter("column");
    String query = request.getParameter("query");
    
    Map<String, Object> map = new HashMap<>();
    
    map.put("column", column);
    map.put("query",query);
    
    List<InactiveUserDto> userList = userMapper.UnAccesstotalUserList(map);
    
    return Map.of("userList",userList);
  }
  
  
  
  
  @Override
  public Map<String, Object> accessList() {
    
    Map<String, Object> map = new HashMap<>();
    
    List<InactiveUserDto> userList = userMapper.UnAccesstotalUserList(map);
    
    return Map.of("userList",userList);
  }
  
  @Override
  public Map<String, Object> LeaveList() {
    
    Map<String, Object> map = new HashMap<>();
    
    List<LeaveUserDto> userList = userMapper.LeaveAccessUserList(map);
    
    return Map.of("userList",userList);
  }
  
  @Transactional
  @Override
  public Map<String, Object> changeAccessUser(HttpServletRequest request) {
   
    String email = request.getParameter("email");
    
    int insertActiveResult = userMapper.insertActiveUser(email);
    
    int deleteInactiveResult = userMapper.deleteInactiveUser(email);
    
    
    return Map.of("insertActiveResult",insertActiveResult,"deleteInactiveResult",deleteInactiveResult);
  }
  
  @Override
  public Map<String, Object> eventCount() {
    
    int eventResult = eventMapper.getEventCount();
    
    return Map.of("eventResult",eventResult);
  }
  
  
}
