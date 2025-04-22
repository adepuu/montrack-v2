package com.adepuu.montrack_v2.auth.infrastructure.security;

import com.adepuu.montrack_v2.auth.application.UserService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log
public class SecurityConfig {
  private final PasswordEncoder passwordEncoder;
  private final JwtConfigProperties jwtConfigProperties;
  private final UserService userService;

  public SecurityConfig(PasswordEncoder passwordEncoder, JwtConfigProperties jwtConfigProperties, UserService userService) {
    this.passwordEncoder = passwordEncoder;
    this.jwtConfigProperties = jwtConfigProperties;
    this.userService = userService;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(authenticationProvider);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
                // Define public endpoints below
                .requestMatchers("/api/v1/auth/login", "/api/v1/user/register").permitAll()
                // Define protected endpoints below
                .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth2 -> {
           oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()));
           oauth2.bearerTokenResolver(request -> {
             Cookie[] cookies = request.getCookies();
             if (cookies != null) {
               for (Cookie cookie : cookies) {
                 if (cookie.getName().equals("SID")) {
                   return cookie.getValue();
                 }
               }
             }

             String token = request.getHeader("Authorization");
             if (token != null && token.startsWith("Bearer ")) {
               return token.substring(7);
             }
             return null;
           });
        })
        .userDetailsService(userService)
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    SecretKey secretKey = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(secretKey).build();
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    SecretKey secretKey = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
    JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<SecurityContext>(secretKey);
    return new NimbusJwtEncoder(immutableSecret);
  }
}
