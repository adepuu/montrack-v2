package com.adepuu.montrack_v2.auth.presentation.dtos;

import com.adepuu.montrack_v2.auth.domain.valueObject.Token;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
  private Token accessToken;
  private Token refreshToken;
}
