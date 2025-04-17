package com.adepuu.montrack_v2.auth.application.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.adepuu.montrack_v2.auth.application.AuthService;
import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.domain.entities.UserProvider;
import com.adepuu.montrack_v2.auth.domain.enums.LoginProviders;
import com.adepuu.montrack_v2.auth.domain.exceptions.LoginFailedException;
import com.adepuu.montrack_v2.auth.domain.valueObject.Token;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserProviderRepository;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserRepository;
import com.adepuu.montrack_v2.auth.presentation.dtos.LoginResponse;

@Service
public class AuthServiceImpl implements AuthService {
private final UserRepository userRepository;
  private final UserProviderRepository userProviderRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserProviderRepository userProviderRepository) {
    this.userRepository = userRepository;
    this.userProviderRepository = userProviderRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public LoginResponse credentialLogin(String email, String password) {
    Optional<User> user = userRepository.findUserByEmail(email);
    if (user.isEmpty()) {
      throw new LoginFailedException("Invalid email or password");
    }

    User foundUser = user.get();

    List<UserProvider> userProviders = userProviderRepository.findByUserAndProvider(foundUser, LoginProviders.LOCAL);
    if (userProviders.isEmpty()) {
      throw new LoginFailedException("Please use the correct login method");
    }

    if (!passwordEncoder.matches(password, foundUser.getPassword())) {
      throw new LoginFailedException("Invalid email or password");
    }

    return LoginResponse.builder()
        .accessToken(Token.builder().value("some.jwt.token").tokenType("Bearer").build())
        .refreshToken(Token.builder().value("some.jwt.token").tokenType("Bearer").build())
        .build();
  }
  
}
