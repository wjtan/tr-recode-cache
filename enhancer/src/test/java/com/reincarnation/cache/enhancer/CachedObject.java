package com.reincarnation.cache.enhancer;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.Cached;

/**
 * <p>
 * Description: CachedObject
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CachedObject {
    private int value;
    
    public void set(int value) {
        this.value = value;
    }
    
    @Cached
    public int get(@CacheKey int key) {
        return value;
    }
    
    @Cached
    public int getKey(@CacheKey int key, String s) {
        return value;
    }
    
    @Cached(timeToLiveSeconds = 1)
    public int getTimed(int key) {
        return value;
    }
    
    @Cached(predicate = Predicate.class)
    public int getPredicate(int key) {
        return value;
    }
    
    @Cached(predicate = Predicate.class, timeToLiveSeconds = 1)
    public int getPredicateTimed(int key) {
        return value;
    }
}
