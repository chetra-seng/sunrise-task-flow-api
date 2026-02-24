package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.services.CountService;
import com.chetraseng.sunrise_task_flow_api.services.CountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorldController {
  private final CountService countService;


  @GetMapping("/world")
  public String getWorld() {
    countService.increment();
    return "This is my world! Count: " + countService.getCount();
  }
}
