package com.reincarnation.cache.caffeine;

import static com.reincarnation.cache.caffeine.CaffeineCacheNames.STATIC_CACHE;
import static com.reincarnation.cache.caffeine.CaffeineCacheNames.TEMPORAL_CACHE;

import com.reincarnation.cache.CacheKeyGenerator;
import com.reincarnation.cache.CacheRemover;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.inject.Inject;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * <p>
 * Description: CaffeineCacheRemover
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
@Singleton
class CaffeineCacheRemover implements CacheRemover {
    private final Cache<String, Object> staticCache;
    private final Cache<String, TimedObject<Object>> temporalCache;
    
    @Inject
    public CaffeineCacheRemover(@Named(STATIC_CACHE) Cache<String, Object> staticCache,
                                @Named(TEMPORAL_CACHE) Cache<String, TimedObject<Object>> temporalCache) {
        this.staticCache = staticCache;
        this.temporalCache = temporalCache;
    }
    
    @Override
    public void remove(String prefix, Object... args) {
        String key = CacheKeyGenerator.getCacheKey(prefix, args);
        staticCache.invalidate(key);
        temporalCache.invalidate(key);
    }
    
}
