package com.adepuu.montrack_v2.auth.application.impl;

import com.adepuu.montrack_v2.auth.application.TokenGeneratorService;
import com.adepuu.montrack_v2.auth.infrastructure.repository.BlackListTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
  private final BlackListTokenRepository blackListTokenRepository;

  public AuthServiceImpl(AuthenticationManager authenticationManager, TokenGeneratorService tokenGeneratorService, BlackListTokenRepository blackListTokenRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenGeneratorService = tokenGeneratorService;
    this.blackListTokenRepository = blackListTokenRepository;
  }

  @Override
  public LoginResponse credentialLogin(String email, String password) {
      try {
        Authentication loginResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String scope = loginResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).reduce((a, b) -> a + " " + b).orElse("");
        return LoginResponse.builder()
                .accessToken(tokenGeneratorService.generateAccessToken(email, scope))
                .refreshToken(tokenGeneratorService.generateRefreshToken(email))
                .build();
      } catch (Exception e) {
        e.printStackTrace();
        throw new LoginFailedException("Invalid email or password" + e.getMessage());
      }
  }

  @Override
  public void logout(String accessToken, String refreshToken) {
    if (tokenGeneratorService.isRefreshToken(refreshToken)) {
      blackListTokenRepository.addToBlackList(refreshToken);
    } else {
      throw new IllegalArgumentException("Invalid token type");
    }
    blackListTokenRepository.addToBlackList(accessToken);
  }
}
