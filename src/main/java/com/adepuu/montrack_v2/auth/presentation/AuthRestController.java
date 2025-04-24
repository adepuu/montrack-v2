package com.adepuu.montrack_v2.auth.presentation;

import com.adepuu.montrack_v2.auth.application.BlackListToken;
import com.adepuu.montrack_v2.auth.application.TokenGeneratorService;
import com.adepuu.montrack_v2.common.security.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.adepuu.montrack_v2.auth.application.AuthService;
import com.adepuu.montrack_v2.auth.presentation.dtos.LoginRequest;
import com.adepuu.montrack_v2.common.Response;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
  private final AuthService authService;
  private final TokenGeneratorService tokenGeneratorService;
  public AuthRestController(AuthService authService, TokenGeneratorService tokenGeneratorService) {
    this.authService = authService;
    this.tokenGeneratorService = tokenGeneratorService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    return Response.successfulResponse(
        "User logged in successfully",
        authService.credentialLogin(request.getEmail(), request.getPassword()));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestBody String refreshToken) {
    String accessToken = Claims.getTokenValue();
    authService.logout(accessToken, refreshToken);
    return Response.successfulResponse("User logged out successfully", null);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
    return Response.successfulResponse("Token refreshed successfully", tokenGeneratorService.generateAccessToken(refreshToken));
  }
}
