package com.adepuu.montrack_v2.auth.application;

import com.adepuu.montrack_v2.auth.domain.valueObject.Token;
import org.springframework.security.core.Authentication;

public interface TokenGeneratorService {
  Token generateAccessToken(String email, Authentication auth);
  Token generateRefreshToken(String email, Authentication auth);
}
