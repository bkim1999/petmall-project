package com.gdu.petmall.service;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.petmall.dao.EventMapper;
import com.gdu.petmall.dto.EventDto;
import com.gdu.petmall.dto.EventImageDto;
import com.gdu.petmall.util.MyFileUtils;
import com.gdu.petmall.util.MyPageUtils;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
  
  private final EventMapper eventMapper;
  private final MyPageUtils myPageUtils;
  private final MyFileUtils myFileUtils;
  
  
  @Transactional(readOnly = true)
  @Override
    public void loadEventList(HttpServletRequest request, Model model) {
    
    Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt.orElse("1"));
    int total = eventMapper.getEventCount();
    int display = 12;
    
    myPageUtils.setPaging(page, total, display);
    
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end"  , myPageUtils.getEnd());
    
    List<EventDto> eventList = eventMapper.getEventList(map);
    
    model.addAttribute("eventList", eventList);
    model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/event/list.do"));
    model.addAttribute("beginNo", total - (page - 1) * display);
    }
  
  @Override
  public EventDto loaddetailEventList(int eventNo) {
    return eventMapper.getEventDetailList(eventNo);
  }
  
  @Override
  public int increaseHit(int eventNo) {
    return eventMapper.updateHit(eventNo);
  }
  
  @Override
  public Map<String, Object> eventImageUpload(MultipartHttpServletRequest multipartRequest) {
    
    // 이미지가 저장될 경로
    LocalDate today = LocalDate.now();
    String imagePath = "/event/" + DateTimeFormatter.ofPattern("yyyy/MM/dd").format(today);
    File dir = new File(imagePath);
    if(!dir.exists()) {
      dir.mkdirs();
    }
    
    // 이미지 파일 (CKEditor는 이미지를 upload라는 이름으로 보냄)
    MultipartFile upload = multipartRequest.getFile("upload");
    
    // 이미지가 저장될 이름
    String originalFilename = upload.getOriginalFilename();
    String filesystemName = myFileUtils.getFilesystemName(originalFilename);
    
    // 이미지 File 객체
    File file = new File(dir, filesystemName);
    
    // 저장
    try {
      upload.transferTo(file);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // CKEditor로 저장된 이미지의 경로를 JSON 형식으로 반환해야 함
    return Map.of("uploaded", true
                , "url", multipartRequest.getContextPath() + imagePath + "/" + filesystemName);
    
    // url: "http://localhost:8080/petmall/event/1/main/filesystemName.jpg"
    // sevlet-context.xml에
    // <resources /event/** -> /product/
  }
  
  
  public List<String> getEventEditorImageList(String contents) {
    
    //** 신규 메소드 **//
    // Editor에 추가한 이미지 목록 반환하기 (Jsoup 라이브러리 사용)
    
    List<String> editorImageList = new ArrayList<>();
    
    Document document = Jsoup.parse(contents);
    Elements elements =  document.getElementsByTag("img");
    
    if(elements != null) {
      for(Element element : elements) {
        String src = element.attr("src");
        String filesystemName = src.substring(src.lastIndexOf("/") + 1);
        editorImageList.add(filesystemName);
      }
    }
    
    return editorImageList;
  }
  
  
  @Override
  public void addEvent(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) throws Exception {
    
    String title = multipartRequest.getParameter("title");
    String contents = multipartRequest.getParameter("contents");
    String startAt = multipartRequest.getParameter("startAt");    
    String endAt = multipartRequest.getParameter("endAt");    
    int discountPercent = Integer.parseInt(multipartRequest.getParameter("discountPercent"));
    int discountPrice = Integer.parseInt(multipartRequest.getParameter("discountPrice"));
    
    List<MultipartFile> files = multipartRequest.getFiles("files");
    
    int attachCount;
    
    if(files.get(0).getSize() == 0) {
      attachCount = 1;
    } else { 
      attachCount = 0;
    }
    
    
      for(MultipartFile multipartFile : files) {
      
      if(multipartFile != null && !multipartFile.isEmpty()) {
        LocalDate today = LocalDate.now();
        String imagePath = "/event/" + DateTimeFormatter.ofPattern("yyyy/MM/dd").format(today);
        File dir = new File(imagePath);
        if(!dir.exists()) {
          dir.mkdirs();
        }
        
        String originalFilename = multipartFile.getOriginalFilename();
        String filesystemName = myFileUtils.getFilesystemName(originalFilename);
        File file = new File(dir, filesystemName);
        
        multipartFile.transferTo(file);
        
        String contentType = Files.probeContentType(file.toPath());
        
        int hasThumbnail = (contentType != null && contentType.startsWith("image")) ? 1 : 0 ;
        
        if(hasThumbnail == 1) {
          File thumbnail = new File(dir, "s_" + filesystemName); // small 이미지를 의미하는 s_을 덧붙임.
          //섬네일레이터(디펜던시)의 활용!
          Thumbnails.of(file)
                    .size(880, 235)      // 가로 880px, 세로 235px
                    .toFile(thumbnail);  
          
        }
        
        
        
        String evntTHumnailUrl = imagePath+ "/s_" + filesystemName;
        
        EventDto eventDto = EventDto.builder()
                                    .title(title)
                                    .contents(contents)
                                    .discountPercent(discountPercent)
                                    .discountPrice(discountPrice)
                                    .eventThumnailUrl(evntTHumnailUrl)
                                    .startAt(startAt)
                                    .endAt(endAt)
                                    .build();
        
        eventMapper.insertEventWrite(eventDto);
        
        
        EventImageDto eventImageDto = EventImageDto.builder()
                                                   .path(evntTHumnailUrl)
                                                   .originalFilename(originalFilename)
                                                   .FilesystemName(filesystemName)
                                                   .build();
        
      } // if
      
    } // for
    

    
  }
 
  
  @Override
  public Map<String, Object> endEvent(HttpServletRequest request) {
    
    int eventNo = Integer.parseInt(request.getParameter("eventNo"));
    
    int endResult = eventMapper.eventEnd(eventNo);
    
    return Map.of("endResult",endResult);
  }
  
  
  @Override
  public Map<String, Object> startEvent(HttpServletRequest request) {
    
    int eventNo = Integer.parseInt(request.getParameter("eventNo"));
    
    int startResult = eventMapper.eventStart(eventNo);
    
    return Map.of("startResult",startResult);
  }
  
  
  @Override
  public Map<String, Object> changePercent(HttpServletRequest request) {
    
    int discountPercent = Integer.parseInt(request.getParameter("discountPercent"));
    int eventNo = Integer.parseInt(request.getParameter("eventNo"));
    
    Map<String, Object> map = Map.of("discountPercent", discountPercent, "eventNo", eventNo);
   
    
    int PercentResult = eventMapper.changeDiscountPercent(map);
    
    return Map.of("PercentResult",PercentResult);
  }
      
      
      
  @Override
   public Map<String, Object> changePrice(HttpServletRequest request) {
    
    int discountPrice = Integer.parseInt(request.getParameter("discountPrice"));
    int eventNo = Integer.parseInt(request.getParameter("eventNo"));
    
    Map<String, Object> map = Map.of("discountPrice", discountPrice, "eventNo", eventNo);
   
    
    int PriceResult = eventMapper.changeDiscountPrice(map);
    
    return Map.of("PriceResult",PriceResult);
   }    
      
      
      
      
      
      
}
