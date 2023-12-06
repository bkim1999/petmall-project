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
  
  @Override
  public Map<String, Object> getloadCategoryList() {
    List<CategoryDto> getCategoryList = faqMapper.getCategoryList(); 
    return Map.of("getCategoryList", getCategoryList);
  }
  
  @Override
  public Map<String, Object> getloadFaqCategoryList() {
    List<FaqCategoryDto> getFaqCategoryList = faqMapper.getFaqCategoryList();
    return Map.of("getFaqCategoryList", getFaqCategoryList);
  }
  
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
  
  public int addFaq(HttpServletRequest request) {
    
    int faqCategoryNo = Integer.parseInt(request.getParameter("faqCategoryNo"));
    int categoryNo = Integer.parseInt(request.getParameter("categoryNo"));
    String faqTitle = request.getParameter("faqTitle");
    String faqContents = request.getParameter("faqContents");
    
    
    FaqDto faq = FaqDto.builder()
                    .faqCategoryDto(FaqCategoryDto.builder().faqCategoryNo(faqCategoryNo).build())
                    .categoryDto(CategoryDto.builder().categoryNo(categoryNo).build())
                    .faqTitle(faqTitle)
                    .faqContents(faqContents)
                    .build();
    
    return faqMapper.insertFaq(faq);
  }
  
  @Override
  public int removeFaq(int faqNo) {
    return faqMapper.deleteFaq(faqNo);
    
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
  
  
  
}


