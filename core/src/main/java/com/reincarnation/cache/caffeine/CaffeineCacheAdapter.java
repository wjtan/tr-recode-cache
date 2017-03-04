package com.reincarnation.cache.caffeine;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.util.ErrorWrapper;
import com.reincarnation.cache.util.TimedObject;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * <p>
 * Description: CaffeineCacheAdapter
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
@Singleton
public class CaffeineCacheAdapter implements CacheAdapter {
    private final Cache<Integer, Object> cache;
    
    @Inject
    public CaffeineCacheAdapter(Cache<Integer, Object> cache) {
        this.cache = cache;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrElse(int hash, Callable<T> callable) {
        return (T) cache.get(hash, hash2 -> ErrorWrapper.get(callable));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrElse(int hash, Callable<T> callable, int timeToLiveInSeconds) {
        TimedObject<T> result = (TimedObject<T>) cache.asMap().compute(hash, (k, v) -> {
            if (v == null) {
                return ErrorWrapper.getTimedObject(callable, timeToLiveInSeconds);
            }
            
            TimedObject<T> currentResult = (TimedObject<T>) v;
            if (currentResult.isExpired()) {
                return ErrorWrapper.getTimedObject(callable, timeToLiveInSeconds);
            } else {
                return currentResult;
            }
        });
        return result.getValue();
    }
    
    @Override
    public void put(int hash, Object value) {
        cache.put(hash, value);
    }
    
    @Override
    public void put(int hash, Object value, int timeToLiveInSeconds) {
        cache.put(hash, new TimedObject<>(value, timeToLiveInSeconds));
    }
    
    @Override
    public void remove(int hash) {
        cache.invalidate(hash);
    }
    
}
