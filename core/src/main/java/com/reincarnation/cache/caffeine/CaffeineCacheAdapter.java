package com.reincarnation.cache.caffeine;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.CacheException;
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
    public <T> T getOrElse(int hash, Callable<T> callable) throws Exception {
        T value = (T) cache.getIfPresent(hash);
        if (value != null) {
            return value;
        }
        
        T value2 = callable.call();
        cache.put(hash, value2);
        return value2;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrElse(int hash, Callable<T> callable, int timeToLiveInSeconds) throws Exception {
        try {
            TimedObject<T> result = (TimedObject<T>) cache.asMap().compute(hash, (k, v) -> {
                try {
                    if (v == null) {
                        return new TimedObject<>(callable.call(), timeToLiveInSeconds);
                    }
                    
                    TimedObject<T> currentResult = (TimedObject<T>) v;
                    if (!currentResult.isExpired()) {
                        return currentResult;
                    }
                    
                    return new TimedObject<>(callable.call(), timeToLiveInSeconds);
                } catch (Exception e) {
                    throw new CacheException(e);
                }
            });
            return result.getValue();
        } catch (CacheException ex) {
            throw (Exception) ex.getCause();
        }
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
    
    @Override
    public void clear() {
        cache.invalidateAll();
    }
    
}
