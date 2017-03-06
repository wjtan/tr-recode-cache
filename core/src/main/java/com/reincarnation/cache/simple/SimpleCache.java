package com.reincarnation.cache.simple;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.CacheException;
import com.reincarnation.cache.util.TimedObject;

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
    public <T> T getOrElse(int key, Callable<T> block) throws Exception {
        if (cache.containsKey(key)) {
            return (T) cache.get(key);
        }
        
        T value2 = block.call();
        cache.put(key, value2);
        return value2;
    }
    
    @Override
    public <T> T getOrElse(int key, Callable<T> block, int timeToLiveInSeconds) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            TimedObject<T> result = (TimedObject<T>) cache.compute(key, (k, v) -> {
                try {
                    if (v == null) {
                        return new TimedObject<>(block.call(), timeToLiveInSeconds);
                    }
                    
                    TimedObject<T> currentResult = (TimedObject<T>) v;
                    if (!currentResult.isExpired()) {
                        return currentResult;
                    }
                    
                    return new TimedObject<>(block.call(), timeToLiveInSeconds);
                } catch (Exception ex) {
                    throw new CacheException(ex);
                }
            });
            return result.getValue();
        } catch (CacheException ex) {
            throw (Exception) ex.getCause();
        }
    }
    
    @Override
    public void put(int key, Object value) {
        cache.put(key, value);
    }
    
    @Override
    public void put(int key, Object value, int timeToLiveInSeconds) {
        cache.put(key, new TimedObject<>(value, timeToLiveInSeconds));
    }
    
    @Override
    public void remove(int key) {
        cache.remove(key);
    }
    
    @Override
    public void clear() {
        cache.clear();
    }
    
}
