package com.adepuu.montrack_v2.auth.infrastructure.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BlackListTokenRepository {
  private final RedisTemplate<String, String> redisTemplate;
  private final String BLACKLIST_PREFIX = "auth:blacklist:";

  public BlackListTokenRepository(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void addToBlackList(String token) {
    redisTemplate.opsForValue().set(BLACKLIST_PREFIX+token, "blacklisted");
  }

  public boolean isBlackListed(String token) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
  }
}
