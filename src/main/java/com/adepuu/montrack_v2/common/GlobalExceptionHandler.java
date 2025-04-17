package com.adepuu.montrack_v2.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
    return Response.failedResponse(400, e.getMessage());
  }
  
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalException(Exception e) {
    return Response.failedResponse(500, e.getMessage());
  }
}
