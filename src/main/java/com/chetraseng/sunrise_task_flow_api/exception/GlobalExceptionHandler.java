package com.chetraseng.sunrise_task_flow_api.exception;

import com.chetraseng.sunrise_task_flow_api.dto.ErrorField;
import com.chetraseng.sunrise_task_flow_api.dto.ErrorResponse;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    List<ErrorField> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new ErrorField(e.getField(), e.getDefaultMessage()))
            .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "validation failed", LocalDateTime.now(), errors));
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), ex.getLocalizedMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(EmailExistException.class)
  public ResponseEntity<ErrorResponse> handleEmailExist(EmailExistException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                    new ErrorResponse(
                            HttpStatus.CONFLICT.value(), ex.getLocalizedMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                    new ErrorResponse(
                            HttpStatus.NOT_FOUND.value(), ex.getLocalizedMessage(), LocalDateTime.now()));
  }
}
