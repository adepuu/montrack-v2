package com.adepuu.montrack_v2.auth.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.adepuu.montrack_v2.auth.domain.exceptions.*;
import com.adepuu.montrack_v2.common.Response;


@ControllerAdvice
public class UserExceptionHandlers {
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
    return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }
}
