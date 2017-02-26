package com.reincarnation.cache.caffeine;

import static com.reincarnation.cache.CacheConfigKeys.CAFFEINE_STATIC_SIZE;
import static com.reincarnation.cache.CacheConfigKeys.CAFFEINE_TEMPORAL_SIZE;
import static com.reincarnation.cache.CacheConfigKeys.STATISTICS;
import static com.reincarnation.cache.caffeine.CaffeineCacheNames.STATIC_CACHE;
import static com.reincarnation.cache.caffeine.CaffeineCacheNames.TEMPORAL_CACHE;

import com.reincarnation.cache.CacheRemover;
import com.reincarnation.cache.CacheType;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * <p>
 * Description: CaffeineCacheModule
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CaffeineCacheModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaffeineCacheModule.class);
    
    @Override
    protected void configure() {
        LOGGER.info("Caffeine Cache");
        
        bind(CacheType.class).toInstance(CacheType.CAFFEINE);
        
        CaffeineCacheInterceptor cacheInterceptor = new CaffeineCacheInterceptor();
        requestInjection(cacheInterceptor);
        
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cached.class), cacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheWrite.class), cacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheRemove.class), cacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheRemoves.class), cacheInterceptor);
        bind(CacheRemover.class).to(CaffeineCacheRemover.class);
    }
    
    @Singleton
    @Provides
    @Named(STATIC_CACHE)
    protected Cache<String, Object> providesStaticCache() {
        // boolean stats = config.getBoolean(STATISTICS);
        // int size = config.getInt(CAFFEINE_STATIC_SIZE);
        
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        
        // if (stats) {
        // caffeine = caffeine.recordStats();
        // }
        
        return caffeine.maximumSize(1000)
                       .build();
    }
    
    @Singleton
    @Provides
    @Named(TEMPORAL_CACHE)
    protected Cache<String, TimedObject<Object>> providesTemporalCache() {
        // boolean stats = config.getBoolean(STATISTICS);
        // int size = config.getInt(CAFFEINE_TEMPORAL_SIZE);
        
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        
        // if (stats) {
        // caffeine = caffeine.recordStats();
        // }
        
        return caffeine.maximumSize(1000)
                       .build();
    }
}
