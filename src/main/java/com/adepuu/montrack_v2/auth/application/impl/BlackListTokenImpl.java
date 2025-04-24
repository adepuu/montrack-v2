package com.adepuu.montrack_v2.auth.application.impl;

import com.adepuu.montrack_v2.auth.application.BlackListToken;
import com.adepuu.montrack_v2.auth.infrastructure.repository.BlackListTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class BlackListTokenImpl implements BlackListToken {
  private final BlackListTokenRepository blackListTokenRepository;

  public BlackListTokenImpl(BlackListTokenRepository blackListTokenRepository) {
    this.blackListTokenRepository = blackListTokenRepository;
  }

  @Override
  public void addToBlackList(String token) {
    blackListTokenRepository.addToBlackList(token);
  }

  @Override
  public boolean isBlackListed(String token) {
    return blackListTokenRepository.isBlackListed(token);
  }
}
