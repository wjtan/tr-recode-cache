package com.reincarnation.cache.simple;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.CacheType;

import com.google.inject.AbstractModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Description: SimpleCacheModule
 * </p>
 * <p>
 * Copyright: 2015
 * </p>
 * 
 * @author Denom
 * @version 1.0
 */
public class SimpleCacheModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCacheModule.class);
    
    @Override
    protected void configure() {
        LOGGER.debug("ConcurrentHashMap Cache");
        
        bind(CacheType.class).toInstance(CacheType.CONCURRENT_HASHMAP);
        bind(CacheAdapter.class).to(SimpleCache.class);
    }
    
}
