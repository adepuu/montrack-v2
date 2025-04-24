package com.adepuu.montrack_v2.auth.application.impl;

import com.adepuu.montrack_v2.auth.application.TokenGeneratorService;
import com.adepuu.montrack_v2.auth.application.UserService;
import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.domain.enums.TokenType;
import com.adepuu.montrack_v2.auth.domain.valueObject.Token;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class TokenGeneratorServiceImpl implements TokenGeneratorService {

  private final JwtEncoder accessTokenEncoder;
  private final JwtEncoder refreshTokenEncoder;
  private final UserService userService;
  private final JwtDecoder refreshTokenDecoder;
  private final JwtDecoder jwtDecoder;

  public TokenGeneratorServiceImpl(
          @Qualifier("jwtEncoder") JwtEncoder accessTokenEncoder,
          @Qualifier("refreshTokenEncoder") JwtEncoder refreshTokenEncoder,
          @Qualifier("refreshTokenDecoder") JwtDecoder refreshTokenDecoder,
          UserService userService, @Qualifier("jwtDecoder") JwtDecoder jwtDecoder) {
    this.accessTokenEncoder = accessTokenEncoder;
    this.refreshTokenEncoder = refreshTokenEncoder;
    this.userService = userService;
    this.refreshTokenDecoder = refreshTokenDecoder;
    this.jwtDecoder = jwtDecoder;
  }


  @Override
  public Token generateAccessToken(String email, String scopes) {
    User user = userService.getUserByEmail(email);

    Instant now = Instant.now();
    // 15 minutes
    long ACCESS_TOKEN_EXPIRATION_TIME = 900L;
    Instant expiresAt = now.plusSeconds(ACCESS_TOKEN_EXPIRATION_TIME);
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("scope", scopes)
            .claim("kind", TokenType.ACCESS.getType())
            .build();

    JwsHeader header = JwsHeader.with(() -> "HS256").build();
    return Token.builder()
            .value(accessTokenEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue())
            .tokenType("Bearer")
            .expiresAt(expiresAt.toString())
            .build();
  }

  @Override
  public Token generateAccessToken(String refreshToken) {
    Jwt decodedToken = refreshTokenDecoder.decode(refreshToken);
    if (decodedToken == null) {
      throw new IllegalArgumentException("Invalid token");
    }

    // check if the token is not expired
    if (decodedToken.getExpiresAt() != null && decodedToken.getExpiresAt().isBefore(Instant.now())) {
      throw new IllegalArgumentException("Token expired");
    }

    String kind = decodedToken.getClaimAsString("kind");
    Integer userId = Integer.parseInt(decodedToken.getSubject());
    if (!kind.equals(TokenType.REFRESH.getType())) {
      throw new IllegalArgumentException("Invalid token type");
    }
    User user = userService.profile(userId);
    String scopes = user.getUserRoles().stream().map(role -> role.getRole().getName()).reduce((a, b) -> a + " " + b).orElse("");
    return generateAccessToken(user.getEmail(), scopes);
  }

  @Override
  public Token generateRefreshToken(String email) {
    User user = userService.getUserByEmail(email);

    Instant now = Instant.now();
    // 7 days
    long REFRESH_TOKEN_EXPIRATION_TIME = 604800L;
    Instant expiresAt = now.plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME);
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getId().toString())
            .claim("kind", TokenType.REFRESH.getType())
            .build();

    JwsHeader header = JwsHeader.with(() -> "HS256").build();
    return Token.builder()
            .value(refreshTokenEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue())
            .tokenType("Bearer")
            .expiresAt(expiresAt.toString())
            .build();
  }

  @Override
  public boolean isRefreshToken(String token) {
    //    decode the token and check the kind claim first
    Jwt decodedToken = refreshTokenDecoder.decode(token);
    String kind = decodedToken.getClaimAsString("kind");

    return kind != null && kind.equals(TokenType.REFRESH.getType());
  }
}
