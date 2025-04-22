package com.adepuu.montrack_v2.auth.application.impl;

import com.adepuu.montrack_v2.auth.application.TokenGeneratorService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.adepuu.montrack_v2.auth.application.AuthService;
import com.adepuu.montrack_v2.auth.domain.exceptions.LoginFailedException;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserProviderRepository;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserRepository;
import com.adepuu.montrack_v2.auth.presentation.dtos.LoginResponse;

@Service
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final TokenGeneratorService tokenGeneratorService;

  public AuthServiceImpl(AuthenticationManager authenticationManager, TokenGeneratorService tokenGeneratorService) {
    this.authenticationManager = authenticationManager;
    this.tokenGeneratorService = tokenGeneratorService;
  }

  @Override
  public LoginResponse credentialLogin(String email, String password) {
      try {
        Authentication loginResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        return LoginResponse.builder()
                .accessToken(tokenGeneratorService.generateAccessToken(email, loginResult))
                .refreshToken(tokenGeneratorService.generateRefreshToken(email, loginResult))
                .build();
      } catch (Exception e) {
        throw new LoginFailedException("Invalid email or password");
      }
  }
}
