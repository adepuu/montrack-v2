package com.adepuu.montrack_v2.auth.domain.valueObject;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String value;
    private String expiresAt;
    @Builder.Default
    private String tokenType = "Bearer";
}
