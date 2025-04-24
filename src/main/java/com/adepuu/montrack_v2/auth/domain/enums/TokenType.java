package com.adepuu.montrack_v2.auth.domain.enums;

public enum TokenType {
  ACCESS("access"),
  REFRESH("refresh");

  private final String type;

  TokenType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
