package com.gdu.petmall.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.gdu.petmall.dao.FaqMapper;
import com.gdu.petmall.dto.CategoryDto;
import com.gdu.petmall.dto.FaqCategoryDto;
import com.gdu.petmall.dto.FaqDto;
import com.gdu.petmall.util.MyPageUtils;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class FaqServiceImpl implements FaqService {
  
  private final FaqMapper faqMapper;
  private final MyPageUtils myPageUtils;
  
  public List<FaqCategoryDto> getloadFaqCategoryList() {
   return faqMapper.getFaqCategoryList();
  }
  
  @Transactional(readOnly=true)
  @Override
  public void customerFaqList(HttpServletRequest request, Model model) {
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    
    int page = Integer.parseInt(opt.orElse("1"));
    int display = 10;
    int total = faqMapper.getFaqCount();
    
    myPageUtils.setPaging(page, total, display);
    
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd());
    
    List<FaqDto> faqList = faqMapper.customerFaqList(map);
    
    model.addAttribute("faqList", faqList);
    model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/faq/list.do"));
    model.addAttribute("beginNo", total - (page - 1) * display);
  }
  
  @Transactional(readOnly=true)
  @Override
  public void adminList(HttpServletRequest request, Model model) {
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    
    int page = Integer.parseInt(opt.orElse("1"));
    int display = 10;
    int total = faqMapper.getFaqCount();
    
    myPageUtils.setPaging(page, total, display);
    
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end", myPageUtils.getEnd());
    
    List<FaqDto> adminList = faqMapper.adminFaqList(map);
    
    model.addAttribute("adminList", adminList);
    model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/faq/write.do"));
    model.addAttribute("beginNo", total - (page - 1) * display);
  }
        
  public void getSearchList(HttpServletRequest request, Model model) {
    
    String column = request.getParameter("column");
    String query = request.getParameter("query");
    
    
    // 검색결과 개수 구하기
    Map<String, Object> map = new HashMap<>();
    map.put("column", column);
    map.put("query", query);

    int total = faqMapper.getSearchCount(map); 
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    String strPage = opt.orElse("1");
    int page = Integer.parseInt(strPage);
    
    int display = 10;
    
    myPageUtils.setPaging(page, total, display);
    
    map.put("begin", myPageUtils.getBegin()); 
    map.put("end", myPageUtils.getEnd());
   
    List<FaqDto> faqList = faqMapper.getSearchList(map);
    
    model.addAttribute("faqList", faqList);
    model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/faq/search.do","column=" + column + "&query"));
    model.addAttribute("beginNo", total - (page - 1) * display);
  }  
  
  
  @Override
  public Map<String, Object> getloadCategorySearchList(HttpServletRequest request, Model model) {
    
      
      String column = request.getParameter("column");
      String query = request.getParameter("query");
      String faqName = request.getParameter("faqName");
      
      
      // 검색결과 개수 구하기
      Map<String, Object> map = new HashMap<>();
      map.put("column", column);
      map.put("query", query);
      map.put("faqName", faqName);
      
      
      int total = faqMapper.getSearchCategoryCount(map); 
      
      Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
      String strPage = opt.orElse("1");
      int page = Integer.parseInt(strPage);
      
      int display = 10;
      
      myPageUtils.setPaging(page, total, display);
      
      map.put("begin", myPageUtils.getBegin()); 
      map.put("end", myPageUtils.getEnd());
     
      List<FaqDto> categorySearchList = faqMapper.getSearchCategoryList(map);
      
      model.addAttribute("categorySearchList", categorySearchList);
      model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/faq/serchCategory.do","column=" + column + "&query"));
      model.addAttribute("beginNo", total - (page - 1) * display);
      
   
    return Map.of("categorySearchList",categorySearchList);
  }
  
  
  
  public int addFaq(HttpServletRequest request) {
    
    int faqCategoryNo = Integer.parseInt(request.getParameter("faqCategoryNo"));
    String faqTitle = request.getParameter("faqTitle");
    String faqContents = request.getParameter("faqContents");
    
    
    FaqDto faq = FaqDto.builder()
                    .faqCategoryDto(FaqCategoryDto.builder().faqCategoryNo(faqCategoryNo).build())
                    .faqTitle(faqTitle)
                    .faqContents(faqContents)
                    .build();
    
    return faqMapper.insertFaq(faq);
  }
  
  @Override
  public Map<String, Object> modifyList(HttpServletRequest request) {
    
    String faqName = request.getParameter("faqName");
    String faqTitle = request.getParameter("faqTitle");
    String faqContents = request.getParameter("faqContents");
    int faqNo = Integer.parseInt(request.getParameter("faqNo"));
    
    FaqDto faq = FaqDto.builder()
                       .faqName(faqName)
                       .faqTitle(faqTitle)
                       .faqContents(faqContents)
                       .faqNo(faqNo)
                       .build();
    
    
   int updateResult = faqMapper.updateFaq(faq);
    
    return Map.of("updateResult", updateResult);
  }
  
  @Override
  public Map<String, Object> deleteFaq(HttpServletRequest request) {
    
    int faqNo = Integer.parseInt(request.getParameter("faqNo"));
    
    Map<String, Object> map = new HashMap<>();
    map.put("faqNo", faqNo);
    
    int removeResult = faqMapper.deleteFaq(map);
    
    return Map.of("removeResult", removeResult);
   
  }
  
  
  
  
  
  
  
  
  
  
  
  
}


