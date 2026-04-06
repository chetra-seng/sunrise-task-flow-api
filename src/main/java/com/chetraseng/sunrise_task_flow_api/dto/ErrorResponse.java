package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
  private final int status;
  private final String message;
  private final LocalDateTime timestamp;
  private List<FieldError> errors;

  public ErrorResponse(int status, String message, LocalDateTime timestamp) {
    this.status = status;
    this.message = message;
    this.timestamp = timestamp;
  }

  public ErrorResponse(int status, String message, LocalDateTime timestamp, List<FieldError> errors) {
    this(status, message, timestamp);
    this.errors = errors;
  }

  public record FieldError(String field, String message) {}
}