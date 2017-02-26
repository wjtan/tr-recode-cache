package com.reincarnation.cache.impl.simple;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.ErrorWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Singleton;

/**
 * <p>
 * Description: SimpleCache
 * </p>
 * <p>
 * Copyright: 2014
 * </p>
 * 
 * @author Denom
 * @version 1.0
 * 
 */
@Singleton
class SimpleCache implements CacheAdapter {
    private final ConcurrentMap<String, Object> cache;
    
    public SimpleCache() {
        cache = new ConcurrentHashMap<>();
    }
    
    /*
     * Non-atomic update
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrElse(String key, Callable<T> block) {
        if (cache.containsKey(key)) {
            return (T) cache.get(key);
        } else {
            T value = ErrorWrapper.get(block);
            cache.put(key, value);
            return value;
        }
    }
    
    @Override
    public <T> T getOrElse(String key, Callable<T> block, int timeToLiveInSeconds) {
        return getOrElse(key, block);
    }
    
    @Override
    public void put(String key, Object value) {
        put(key, value, 0);
    }
    
    @Override
    public void put(String key, Object value, int timeToLiveInSeconds) {
        cache.put(key, value);
    }
    
    @Override
    public void remove(String key) {
        cache.remove(key);
    }
    
}
