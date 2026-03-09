package com.chetraseng.sunrise_task_flow_api.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
  private final int status;
  private final String message;
  private final LocalDateTime timestamp;
  private List<ErrorField> errors;

  public ErrorResponse(int status, String message, LocalDateTime timestamp) {
    this.status = status;
    this.message = message;
    this.timestamp = timestamp;
  }

  public ErrorResponse(int status, String message, LocalDateTime timestamp, List<ErrorField> errors) {
    this(status, message, timestamp);
    this.errors = errors;
  }
}
