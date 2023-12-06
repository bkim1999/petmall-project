package com.gdu.petmall.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gdu.petmall.service.EventService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventImageBatch {
    
  private final EventService eventService;
  
  @Scheduled(cron="0 2 0 1/1 * ?")
  public void execute() {
    eventService.eventImageBatch();
  }
  
  @Scheduled(cron="0 0 0 1/1 * ?")
  public void executesAutoEnd() {
    eventService.eventAutoEnd();
  }
  
  @Scheduled(cron="0 0 0 1/1 * ?")
  public void executesAutoStart() {
    eventService.eventAutoStart();
  }
  
}
