package com.reincarnation.cache.caffeine;

import static com.reincarnation.cache.caffeine.CacheConfigKeys.CAFFEINE_SIZE;
import static com.reincarnation.cache.caffeine.CacheConfigKeys.CAFFEINE_STATISTICS;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.CacheType;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.typesafe.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * <p>
 * Description: CaffeineModule
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CaffeineCacheModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaffeineCacheModule.class);
    
    @Override
    protected void configure() {
        LOGGER.debug("Caffeine Cache");
        
        bind(CacheType.class).toInstance(CacheType.CAFFEINE);
        bind(CacheAdapter.class).to(CaffeineCacheAdapter.class);
    }
    
    @Singleton
    @Provides
    protected Cache<Integer, Object> providesCache(Config config) {
        boolean stats = config.getBoolean(CAFFEINE_STATISTICS);
        int size = config.getInt(CAFFEINE_SIZE);
        
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        
        if (stats) {
            caffeine = caffeine.recordStats();
        }
        
        return caffeine.maximumSize(size)
                       .build();
    }
    
}
