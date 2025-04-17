package com.adepuu.montrack_v2.auth.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.adepuu.montrack_v2.auth.domain.exceptions.*;
import com.adepuu.montrack_v2.common.Response;


@ControllerAdvice
public class AuthExceptionHandlers {
  @ExceptionHandler(LoginFailedException.class)
  public ResponseEntity<?> handleLoginFailedException(LoginFailedException e) {
    return Response.failedResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
  }

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<?> handleDuplicateUserException(DuplicateUserException e) {
    return Response.failedResponse(HttpStatus.CONFLICT.value(), e.getMessage());
  }
}
