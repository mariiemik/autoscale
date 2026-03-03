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
    @Value("${app.cache.names:}") // Берем имена из пропертей сервиса
    private String[] cacheNames;

//    @Bean
//    public io.micrometer.core.instrument.binder.MeterBinder redisCacheMetricsBinder(RedisCacheManager cacheManager) {
//        return (registry) -> {
//            // Это свяжет менеджер с Prometheus
//        };
//    }

//    @Bean
//    public MeterBinder redisCacheMetricsBinder(RedisCacheManager cacheManager) {
//        return (registry) -> {
//            if (cacheNames != null) {
//                for (String name : cacheNames) {
//                    org.springframework.cache.Cache cache = cacheManager.getCache(name.trim());
//                    if (cache != null) {
//                        // Мы используем встроенный механизм регистрации через Spring
//                        // Если RedisCacheMetrics недоступен, Spring сам подцепит статистику
//                        // через .enableStatistics() в бине cacheManager.
//                    }
//                }
//            }
//        };
//    }
//@Bean
//public MeterBinder redisCacheMetricsBinder(RedisCacheManager cacheManager) {
//    return (registry) -> {
//        if (cacheNames != null) {
//            for (String name : cacheNames) {
//                org.springframework.cache.Cache cache = cacheManager.getCache(name.trim());
//                if (cache != null) {
//                    // Статический метод monitor сам разберется с типами и аргументами
//                    io.micrometer.core.instrument.binder.cache.RedisCacheMetrics.monitor(
//                            registry,
//                            (org.springframework.data.redis.cache.RedisCache) cache,
//                            name.trim()
//                    );
//                }
//            }
//        }
//    };
//}

    //    @Bean
//    public MeterBinder redisCacheMetricsBinder(RedisCacheManager cacheManager) {
//        return (registry) -> {
//            if (cacheNames != null) {
//                for (String name : cacheNames) {
//                    // Пытаемся получить кэш
//                    org.springframework.cache.Cache cache = cacheManager.getCache(name.trim());
//                    if (cache != null) {
//                        // Регистрируем через стандартный регистратор Micrometer для Spring Cache
//                        io.micrometer.core.instrument.binder.cache.CacheMetricsRegistrar.monitor(
//                                registry,
//                                cache,
//                                name.trim(),
//                                java.util.Collections.emptyList()
//                        );
//                    }
//                }
//            }
//        };
//    }
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
            // Явно указываем String, чтобы не ругалась старая Java
            builder.initialCacheNames(new HashSet<String>(Arrays.asList(cacheNames)));
        }

        return builder.build();
    }

}
