package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.services.CountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {
  private final CountService countService;

  @GetMapping("/hello")
  public String getHello() {
    countService.increment();
    return "Hello, Spring Boot! Count: " + countService.getCount();
  }
}
