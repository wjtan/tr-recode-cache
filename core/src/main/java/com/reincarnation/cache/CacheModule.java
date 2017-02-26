package com.reincarnation.cache;

import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * <p>
 * Description: CacheModule
 * </p>
 * <p>
 * Copyright: 2014
 * </p>
 * 
 * @author Denom
 * @version 1.0
 * 
 */
public class CacheModule extends AbstractModule {
    @Override
    protected void configure() {
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        requestInjection(cacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cached.class), cacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheWrite.class), cacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheRemove.class), cacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(CacheRemoves.class), cacheInterceptor);
        bind(CacheRemover.class).to(CacheAdapterRemover.class);
    }
}
