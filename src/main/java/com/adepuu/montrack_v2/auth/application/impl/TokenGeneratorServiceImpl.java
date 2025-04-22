package com.adepuu.montrack_v2.auth.application.impl;

import com.adepuu.montrack_v2.auth.application.TokenGeneratorService;
import com.adepuu.montrack_v2.auth.application.UserService;
import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.domain.valueObject.Token;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenGeneratorServiceImpl implements TokenGeneratorService {
  private final long ACCESS_TOKEN_EXPIRATION_TIME = 900L; // 15 minutes
  private final long REFRESH_TOKEN_EXPIRATION_TIME = 604800L; // 7 days

  private final JwtEncoder jwtEncoder;
  private final UserService userService;

  public TokenGeneratorServiceImpl(JwtEncoder jwtEncoder, UserService userService) {
    this.jwtEncoder = jwtEncoder;
    this.userService = userService;
  }


  @Override
  public Token generateAccessToken(String email, Authentication auth) {
    return getToken(email, auth, ACCESS_TOKEN_EXPIRATION_TIME);
  }

  @Override
  public Token generateRefreshToken(String email, Authentication auth) {
    return getToken(email, auth, REFRESH_TOKEN_EXPIRATION_TIME);
  }

  private Token getToken(String email, Authentication auth, long ttl) {
    User user = userService.getUserByEmail(email);
    String scope = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).reduce((a, b) -> a + " " + b).orElse("");

    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(ttl);
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("scope", scope)
            .build();

    JwsHeader header = JwsHeader.with(() -> "HS256").build();
    return Token.builder()
            .value(jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue())
            .tokenType("Bearer")
            .expiresAt(expiresAt.toString())
            .build();
  }
}
