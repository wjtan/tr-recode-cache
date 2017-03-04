package com.reincarnation.cache.simple;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.util.ErrorWrapper;

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
    private final ConcurrentMap<Integer, Object> cache;
    
    public SimpleCache() {
        cache = new ConcurrentHashMap<>();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrElse(int key, Callable<T> block) {
        return (T) cache.computeIfAbsent(key, key2 -> ErrorWrapper.get(block));
    }
    
    @Override
    public <T> T getOrElse(int key, Callable<T> block, int timeToLiveInSeconds) {
        return getOrElse(key, block);
    }
    
    @Override
    public void put(int key, Object value) {
        cache.put(key, value);
    }
    
    @Override
    public void put(int key, Object value, int timeToLiveInSeconds) {
        cache.put(key, value);
    }
    
    @Override
    public void remove(int key) {
        cache.remove(key);
    }
    
}
