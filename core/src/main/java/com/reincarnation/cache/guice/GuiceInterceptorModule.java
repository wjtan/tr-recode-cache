package com.reincarnation.cache.guice;

import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.annotation.ThreadLocalCached;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Description: GuiceInterceptorModule
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class GuiceInterceptorModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiceInterceptorModule.class);
    
    @Override
    protected void configure() {
        LOGGER.info("Cache: Guice Interceptor");
        
        CachedInterceptor cachedInterceptor = new CachedInterceptor();
        requestInjection(cachedInterceptor);
        
        CacheWriteInterceptor cacheWriteInterceptor = new CacheWriteInterceptor();
        requestInjection(cacheWriteInterceptor);
        
        CacheRemoveInterceptor cacheRemoveInterceptor = new CacheRemoveInterceptor();
        requestInjection(cacheRemoveInterceptor);
        
        ThreadLocalCachedInterceptor threadLocalCacheInterceptor = new ThreadLocalCachedInterceptor();
        requestInjection(threadLocalCacheInterceptor);
        
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cached.class), cachedInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheWrite.class), cacheWriteInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheRemove.class), cacheRemoveInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheRemoves.class), cacheRemoveInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(ThreadLocalCached.class), threadLocalCacheInterceptor);
    }
    
}
