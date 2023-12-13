package com.gdu.petmall.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    int display = 6;
    
    myPageUtils.setPaging(page, total, display);
    
    Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
                                   , "end"  , myPageUtils.getEnd());
    
    List<EventDto> eventList = eventMapper.getEventList(map);
    
    model.addAttribute("eventList", eventList);
    model.addAttribute("paging", myPageUtils.getMvcPaging(request.getContextPath() + "/event/list.do"));
    model.addAttribute("beginNo", total - (page - 1) * display);
    }
  
  @Override
  public void loaddetailEventList(int eventNo, Model model) {
    
    EventDto event = eventMapper.getEventDetailList(eventNo);
    
    List<EventImageDto> eventImagelist = eventMapper.geteventImageList(eventNo);
    
    model.addAttribute("event",event);
    model.addAttribute("eventImagelist",eventImagelist);
    
    
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
    // <resources /event/** -> /event/
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
  public void addEvent(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes, HttpServletRequest request) throws Exception {
    
    String title = multipartRequest.getParameter("title");
    String contents = multipartRequest.getParameter("contents");
    String startAt = multipartRequest.getParameter("startAt").replaceAll("-", "/");    
    String endAt = multipartRequest.getParameter("endAt").replaceAll("-", "/");    
    int discountPercent = Integer.parseInt(multipartRequest.getParameter("discountPercent"));
    int discountPrice = Integer.parseInt(multipartRequest.getParameter("discountPrice"));
    String contextPath = request.getContextPath();
    int state = Integer.parseInt(multipartRequest.getParameter("state"));
    
    
    List<MultipartFile> event_images = multipartRequest.getFiles("event_images");
    
    
    List<MultipartFile> files = multipartRequest.getFiles("files");
    
    int attachCount;
    
    if(files.get(0).getSize() == 0) {
      attachCount = 1;
    } else { 
      attachCount = 0;
    }
    
      
      for(MultipartFile multipartFile : files) {
      
      if(multipartFile != null && !multipartFile.isEmpty()) {
        String imagePath = "/event/" + endAt;
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
                    .size(800, 235)      // 가로 1621px, 세로 235px
                    .toFile(thumbnail);  
          
        }
        
        
        String evntTHumnailUrl = contextPath+imagePath+"/s_"+filesystemName;
        
        EventDto eventDto = EventDto.builder()
                                    .title(title)
                                    .contents(contents)
                                    .discountPercent(discountPercent)
                                    .discountPrice(discountPrice)
                                    .eventThumnailUrl(evntTHumnailUrl)
                                    .startAt(startAt)
                                    .endAt(endAt)
                                    .state(state)
                                    .build();
        
       eventMapper.insertEventWrite(eventDto);
       
       int serveEventNo =eventDto.getEventNo();
       
       EventImageDto eventSdto = EventImageDto.builder()
                                              .eventNo(serveEventNo)
                                              .FilesystemName(filesystemName)
                                              .originalFilename(originalFilename)
                                              .path(evntTHumnailUrl)
                                              .build();
       
       
       eventMapper.insertEventImage(eventSdto);
       
      
      for(MultipartFile event_multipartFile : event_images) {
        
        if(event_multipartFile != null && ! event_multipartFile.isEmpty()) {
          String event_imagePath = "/event/" + endAt;
          File event_dir = new File(event_imagePath);
          if(!event_dir.exists()) {
            event_dir.mkdirs();
          }
          
          String event_originalFilename = event_multipartFile.getOriginalFilename();
          String event_filesystemName = myFileUtils.getFilesystemName(event_originalFilename);
          File event_file = new File(event_dir, event_filesystemName);
          
          event_multipartFile.transferTo(event_file);
          
          String event_contentType = Files.probeContentType(event_file.toPath());
          
          int event_hasThumbnail = (event_contentType != null && event_contentType.startsWith("image")) ? 1 : 0 ;
          
          if(event_hasThumbnail == 1) {
            File event_thumbnail = new File(event_dir, "c_" + event_filesystemName); // small 이미지를 의미하는 s_을 덧붙임.
            //섬네일레이터(디펜던시)의 활용!
            Thumbnails.of(event_file)
            .size(800, 235)      // 가로 1621px, 세로 235px
            .toFile(event_thumbnail);  
            
          }
          String event_path = contextPath+event_imagePath+"/c_"+event_filesystemName;
          
          EventImageDto eventImageDto = EventImageDto.builder()
                                                     .eventNo(serveEventNo)
                                                     .path(event_path)
                                                     .originalFilename(event_originalFilename)
                                                     .FilesystemName(event_filesystemName)
                                                     .build();
          
          eventMapper.insertEventImage(eventImageDto);
          
            }
          }
          
          
          
        } //if
        
      } //for
        
    

    
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
      
  @Transactional
  @Override
  public void updateDetailEvent(MultipartHttpServletRequest multipartRequest, Model model) throws Exception {
    
    String title = multipartRequest.getParameter("title");
    String contents = multipartRequest.getParameter("contents");
    String startAt = multipartRequest.getParameter("startAt").replaceAll("-", "/");    
    String endAt = multipartRequest.getParameter("endAt").replaceAll("-", "/");       
    int discountPercent = Integer.parseInt(multipartRequest.getParameter("discountPercent"));
    int discountPrice = Integer.parseInt(multipartRequest.getParameter("discountPrice"));
    String contextPath = multipartRequest.getContextPath();
    int eventNo = Integer.parseInt(multipartRequest.getParameter("eventNo"));
    int state = Integer.parseInt(multipartRequest.getParameter("state"));
    
    
    List<MultipartFile> event_images = multipartRequest.getFiles("event_images");
    
    
    List<MultipartFile> files = multipartRequest.getFiles("files");
    
    int attachCount;
    
    if(files.get(0).getSize() == 0) {
      attachCount = 1;
    } else { 
      attachCount = 0;
    }
      
    eventMapper.deleteEventImage(eventNo);
      
      for(MultipartFile multipartFile : files) {
      
      if(multipartFile != null && !multipartFile.isEmpty()) {
        String imagePath = "/event/" + endAt;
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
          File thumbnail = new File(dir, "res_" + filesystemName); // small 이미지를 의미하는 s_을 덧붙임.
          //섬네일레이터(디펜던시)의 활용!
          Thumbnails.of(file)
                    .size(800, 235)      // 가로 1621px, 세로 235px
                    .toFile(thumbnail);  
          
        }
        
        
        String evntTHumnailUrl = contextPath+imagePath+"/res_"+filesystemName;
        
        EventDto eventDto = EventDto.builder()
                                    .eventNo(eventNo)
                                    .title(title)
                                    .contents(contents)
                                    .discountPercent(discountPercent)
                                    .discountPrice(discountPrice)
                                    .eventThumnailUrl(evntTHumnailUrl)
                                    .startAt(startAt)
                                    .endAt(endAt)
                                    .state(state)
                                    .build();
        
        eventMapper.updateDetailEvent(eventDto);


        EventImageDto eventSdto = EventImageDto.builder()
                                    .eventNo(eventNo)
                                    .FilesystemName(filesystemName)
                                    .originalFilename(originalFilename)
                                    .path(evntTHumnailUrl)
                                    .build();


        eventMapper.insertEventImage(eventSdto);
       
       
      
      for(MultipartFile event_multipartFile : event_images) {
        
        if(event_multipartFile != null && ! event_multipartFile.isEmpty()) {
          String event_imagePath = "/event/" + endAt;
          File event_dir = new File(event_imagePath);
          if(!event_dir.exists()) {
            event_dir.mkdirs();
          }
          
          String event_originalFilename = event_multipartFile.getOriginalFilename();
          String event_filesystemName = myFileUtils.getFilesystemName(event_originalFilename);
          File event_file = new File(event_dir, event_filesystemName);
          
          event_multipartFile.transferTo(event_file);
          
          String event_contentType = Files.probeContentType(event_file.toPath());
          
          int event_hasThumbnail = (event_contentType != null && event_contentType.startsWith("image")) ? 1 : 0 ;
          
          if(event_hasThumbnail == 1) {
            File event_thumbnail = new File(event_dir, "rec_" + event_filesystemName); // small 이미지를 의미하는 s_을 덧붙임.
            //섬네일레이터(디펜던시)의 활용!
            Thumbnails.of(event_file)
            .size(800, 235)      // 가로 1621px, 세로 235px
            .toFile(event_thumbnail);  
            
          }
          String event_path = contextPath+event_imagePath+"/rec_"+event_filesystemName;
          
          EventImageDto eventImageDto = EventImageDto.builder()
                                                     .eventNo(eventNo)
                                                     .path(event_path)
                                                     .originalFilename(event_originalFilename)
                                                     .FilesystemName(event_filesystemName)
                                                     .build();
          
           eventMapper.insertEventImage(eventImageDto);
          
          }
         }
          
        } //if
        
      } //for
    
  }
  
  // 불필요한 이미지 객체 삭제를 위한 메소드 (myfileutils로 옮겨야함) 테스트 진행중       date = date.minusDays(1);
  public String getEventImagePathYesterday() {
    LocalDate date = LocalDate.now();
    date = date.minusDays(1);
    return "/event/" + DateTimeFormatter.ofPattern("yyyy/MM/dd").format(date);
  }
  
  
  @Override
  public void eventImageBatch() {
    // 1. 어제 작성된 블로그의 이미지 목록 (DB)
    List<EventImageDto> eventImageList = eventMapper.getEventImageInYesterday();
    
    
    // 2. List<EventImageDto> -> List<Path> (path는 경로+파일명으로 구성)
    List<Path> eventImagePathList = eventImageList.stream()
        .map(eventImageDto -> new File(eventImageDto.getPath()).toPath())
        .collect(Collectors.toList());
    
    // 3. 어제 저장된 블로그 이미지 목록 (디렉토리)
    File dir = new File(getEventImagePathYesterday());
    
    // 4. 삭제할 File 객체들
    File[] targets = dir.listFiles(file -> eventImagePathList.contains(file.toPath()));
    
    // 5. 삭제
    if(targets != null && targets.length !=0) {
      for(File target : targets) {
        target.delete();
      }
    }
    
  }
  
  @Override
  public void eventAutoEnd() {
    eventMapper.autoEnd();
  }
  
  @Override
  public void eventAutoStart() {
    eventMapper.autoStart();
  }
  
  
      
      
}
