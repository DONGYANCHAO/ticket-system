package com.ticket.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 *
 * @author Ticket System
 */
@Configuration
public class CacheConfig {

    @Value("${cache.spec:maximumSize=1000,expireAfterWrite=10m}")
    private String cacheSpec;

    @Value("${cache.user-cache-spec:maximumSize=500,expireAfterWrite=10m}")
    private String userCacheSpec;

    @Value("${cache.role-cache-spec:maximumSize=100,expireAfterWrite=30m}")
    private String roleCacheSpec;

    @Value("${cache.module-cache-spec:maximumSize=100,expireAfterWrite=1h}")
    private String moduleCacheSpec;

    /**
     * 默认缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.from(cacheSpec));
        return cacheManager;
    }

    /**
     * 用户缓存
     */
    @Bean("userCache")
    public CacheManager userCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("userCache");
        cacheManager.setCaffeine(Caffeine.from(userCacheSpec));
        return cacheManager;
    }

    /**
     * 角色缓存
     */
    @Bean("roleCache")
    public CacheManager roleCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("roleCache");
        cacheManager.setCaffeine(Caffeine.from(roleCacheSpec));
        return cacheManager;
    }

    /**
     * 模块缓存
     */
    @Bean("moduleCache")
    public CacheManager moduleCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("moduleCache");
        cacheManager.setCaffeine(Caffeine.from(moduleCacheSpec));
        return cacheManager;
    }
}
