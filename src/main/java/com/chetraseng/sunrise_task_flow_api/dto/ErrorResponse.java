package com.chetraseng.sunrise_task_flow_api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
  private final int status;
  private final String message;
  private final LocalDateTime timestamp;
}
