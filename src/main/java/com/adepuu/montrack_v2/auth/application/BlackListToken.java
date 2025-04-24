package com.adepuu.montrack_v2.auth.application;

public interface BlackListToken {
  void addToBlackList(String token);
  boolean isBlackListed(String token);
}
