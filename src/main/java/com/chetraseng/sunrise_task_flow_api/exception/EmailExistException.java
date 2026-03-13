package com.chetraseng.sunrise_task_flow_api.exception;

public class EmailExistException extends RuntimeException {
  public EmailExistException(String message) {
    super(message);
  }
}
