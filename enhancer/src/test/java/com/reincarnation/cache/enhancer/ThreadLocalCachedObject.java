package com.reincarnation.cache.enhancer;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.ThreadLocalCached;

/**
 * <p>
 * Description: ThreadLocalCachedObject
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class ThreadLocalCachedObject {
    private int value;
    
    public void set(int value) {
        this.value = value;
    }
    
    @ThreadLocalCached
    public int get(@CacheKey int key) {
        return value;
    }
    
    @ThreadLocalCached
    public int getKey(@CacheKey int key, String s) {
        return value;
    }
}
