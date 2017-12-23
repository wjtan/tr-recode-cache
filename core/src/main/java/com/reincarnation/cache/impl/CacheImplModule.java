package com.reincarnation.cache.impl;

import com.reincarnation.cache.ThreadLocalCacheAdapter;

import com.google.inject.AbstractModule;

/**
 * <p>
 * Description: CacheImplModule
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 * @version 1.0
 */
public class CacheImplModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ThreadLocalCacheAdapter.class).to(ThreadLocalCacheAdapterImpl.class);
    }
    
}
