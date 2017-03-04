package com.reincarnation.cache.enhancer;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.CacheValue;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;

/**
 * <p>
 * Description: TestWrite
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheWriteObject {
    private static final String KEY = "SomeKey";
    
    private int value;
    
    @CacheWrite(KEY)
    public void write(int value) {
        this.value = value;
    }
    
    @CacheWrite(KEY)
    public void writeKey(@CacheKey int moreKey, int value) {
        this.value = value + moreKey;
    }
    
    @CacheWrite(KEY)
    public void writeValue(@CacheValue int value, @CacheKey int moreKey) {
        this.value = value + moreKey;
    }
    
    @CacheWrite(value = KEY, timeToLiveSeconds = 10)
    public void writeTimed(@CacheValue int value) {
        this.value = value;
    }
    
    public int get() {
        return value;
    }
    
    @Cached(KEY)
    public int getCached() {
        return 0;
    }
    
    @Cached(KEY)
    public int getCachedKey(int key) {
        return 0;
    }
    
    @Cached(value = KEY, timeToLiveSeconds = 10)
    public int getTimed() {
        return 0;
    }
}
