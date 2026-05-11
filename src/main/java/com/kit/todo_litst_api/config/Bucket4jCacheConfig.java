package com.kit.todo_litst_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

@Configuration
public class Bucket4jCacheConfig {

    @Bean(destroyMethod = "close")
    public CacheManager bucket4jCacheManager() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        createCacheIfMissing(cacheManager, "todoReadBuckets");
        createCacheIfMissing(cacheManager, "todoWriteBuckets");
        return cacheManager;
    }

    private void createCacheIfMissing(CacheManager cacheManager, String cacheName) {
        if (cacheManager.getCache(cacheName) == null) {
            cacheManager.createCache(cacheName, new MutableConfiguration<String, byte[]>());
        }
    }
}
