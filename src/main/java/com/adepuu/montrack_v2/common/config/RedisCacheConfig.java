package com.adepuu.montrack_v2.common.config;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {
  @Bean
  public RedisSerializer<Object> redisSerializer(ObjectMapper objectMapper) {
    // Create a copy of the object mapper to avoid affecting other components
    ObjectMapper cacheObjectMapper = objectMapper.copy();
    cacheObjectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    // Configure the object mapper for Redis to include type information
    // This ensures proper deserialization of complex objects like User entity
    cacheObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);
    
    return new Jackson2JsonRedisSerializer<>(cacheObjectMapper, Object.class);
  }

  @Bean
  public RedisCacheConfiguration cacheConfiguration(RedisSerializer<Object> redisSerializer) {
    return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // Set default TTL to 1/2 hour
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(redisSerializer));
  }

//    @Bean
//  public RedisCacheConfiguration cacheConfiguration() {
//    return RedisCacheConfiguration.defaultCacheConfig()
//            .entryTtl(Duration.ofMinutes(30)) // Set default TTL to 1/2 hour
//            .disableCachingNullValues()
//            .serializeValuesWith(SerializationPair.fromSerializer(new JdkSerializationRedisSerializer()));
//  }

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    // Create base cache configuration with our Redis serializer
    RedisCacheConfiguration baseConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(redisSerializer(new ObjectMapper())))
            .disableCachingNullValues();

    return (builder) -> builder
            // User listing cache - shorter TTL since this data changes frequently
            .withCacheConfiguration("allUsersCache",
                    baseConfig.entryTtl(Duration.ofMinutes(5)))
            
            // User detail caches - longer TTL since individual user data changes less frequently
            .withCacheConfiguration("userByEmailCache",
                    baseConfig.entryTtl(Duration.ofMinutes(15)))
            .withCacheConfiguration("userProfileCache",
                    baseConfig.entryTtl(Duration.ofMinutes(15)))
            .withCacheConfiguration("userDetailsCache",
                    baseConfig.entryTtl(Duration.ofMinutes(10)))
            
            // Prevent creating caches for undefined keys
            .disableCreateOnMissingCache();
  }
}
