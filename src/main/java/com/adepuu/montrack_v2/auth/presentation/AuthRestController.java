package com.adepuu.montrack_v2.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adepuu.montrack_v2.auth.application.AuthService;
import com.adepuu.montrack_v2.auth.presentation.dtos.LoginRequest;
import com.adepuu.montrack_v2.common.Response;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
  private final AuthService authService;

  public AuthRestController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    return Response.successfulResponse(
        "User logged in successfully",
        authService.credentialLogin(request.getEmail(), request.getPassword()));
  }
}
