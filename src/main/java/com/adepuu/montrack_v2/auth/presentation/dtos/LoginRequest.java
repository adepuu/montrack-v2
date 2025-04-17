package com.adepuu.montrack_v2.auth.presentation.dtos;

import lombok.Data;

@Data
public class LoginRequest {
  private String email;
  private String password;
}
