package com.example.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${app.cache.names:}")
    private String[] cacheNames;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()
                ));

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .enableStatistics();

//        if (cacheNames != null && cacheNames.length > 0) {
//            builder.initialCacheNames(new HashSet<>(Arrays.asList(cacheNames)));
//        }
        if (cacheNames != null && cacheNames.length > 0) {
            builder.initialCacheNames(new HashSet<String>(Arrays.asList(cacheNames)));
        }

        return builder.build();
    }

}
