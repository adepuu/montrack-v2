package com.adepuu.montrack_v2.auth.application;

import com.adepuu.montrack_v2.auth.presentation.dtos.LoginResponse;

public interface AuthService {
  LoginResponse credentialLogin(String email, String password);
}
