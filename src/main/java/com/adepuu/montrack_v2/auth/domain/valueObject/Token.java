package com.adepuu.montrack_v2.auth.domain.valueObject;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    private String value;
    private String expiresAt;
    @Builder.Default
    private String tokenType = "Bearer";
}
