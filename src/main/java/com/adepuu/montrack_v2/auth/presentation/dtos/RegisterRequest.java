package com.adepuu.montrack_v2.auth.presentation.dtos;

import com.adepuu.montrack_v2.auth.domain.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String pin;

    public User toUser() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .pin(pin)
                .build();
    }
}
