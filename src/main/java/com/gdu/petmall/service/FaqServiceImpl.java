package com.gdu.petmall.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.gdu.petmall.dao.FaqMapper;
import com.gdu.petmall.dto.FaqDto;
import com.gdu.petmall.util.MyPageUtils;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class FaqServiceImpl implements FaqService {
  
  private final FaqMapper faqMapper;
  private final MyPageUtils myPageUtils;
  
  
  @Transactional(readOnly=true)
  @Override
  public void getFaqList(HttpServletRequest request, Model model) {
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    
    int page = Integer.parseInt(opt.orElse("1"));
    int display = 10;
    int total = faqMapper.getFaqCount();
    
    myPageUtils.setPaging(page, total, display);
    
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd());
    
    List<FaqDto> faqList = faqMapper.getFaqList(map);
    
    model.addAttribute("faqList", faqList);
    model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/free/list.do"));
    model.addAttribute("beginNo", total - (page - 1) * display);
  }
        
  @Override
  public int addReply(HttpServletRequest request) {
    
    
    int depth = Integer.parseInt(request.getParameter("depth"));
    int groupNo = Integer.parseInt(request.getParameter("groupNo"));
    int groupOrder = Integer.parseInt(request.getParameter("groupOrder"));
    
    // 원글DTO
    // 
    FaqDto faq = FaqDto.builder()
                          .groupNo(groupNo)
                          .groupOrder(groupOrder)
                          .build();
    faqMapper.updateGroupOrder(faq);
    
    FaqDto reply = FaqDto.builder()
                         .depth(depth + 1)
                         .groupNo(groupNo)
                         .groupOrder(groupOrder + 1)
                         .build();
    
    int addReplyResult = faqMapper.insertReply(reply);
    
    return addReplyResult;
  }

  
  
}


