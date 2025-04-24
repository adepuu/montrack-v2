package com.adepuu.montrack_v2.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

public class Claims {
  public static Map<String, Object> getClaims() {
    SecurityContext ctx = SecurityContextHolder.getContext();
    Authentication auth = ctx.getAuthentication();

    if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
      throw new IllegalStateException("No JWT found in SecurityContext");
    }
    return ((Jwt) auth.getPrincipal()).getClaims();
  }

  public static String getTokenValue() {
    SecurityContext ctx = SecurityContextHolder.getContext();
    Authentication auth = ctx.getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      Jwt jwt = jwtAuthenticationToken.getToken();
      return jwt.getTokenValue();
    }
    return null;
  }

  public static Integer getUserId() {
    Map<String, Object> claims = getClaims();
    System.out.println(claims.toString());
    return Integer.parseInt(String.valueOf(claims.get("sub")));
  }
}
