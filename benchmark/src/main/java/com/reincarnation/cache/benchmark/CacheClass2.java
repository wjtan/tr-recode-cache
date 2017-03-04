package com.reincarnation.cache.benchmark;

import static com.reincarnation.cache.benchmark.Constants.CACHEKEY;
import static com.reincarnation.cache.benchmark.Constants.VALUE1;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.annotation.IgnoreCacheEnhancer;

/**
 * <p>
 * Description: CacheClass
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
@IgnoreCacheEnhancer
public class CacheClass2 {
    @Cached
    public int get() {
        return VALUE1;
    }
    
    @Cached(value = CACHEKEY)
    public int getKey() {
        return VALUE1;
    }
    
    @Cached(timeToLiveSeconds = 1)
    public int getTimed() {
        return VALUE1;
    }
    
    @Cached(value = CACHEKEY + "2", timeToLiveSeconds = 1)
    public int getKeyTimed() {
        return VALUE1;
    }
    
    @Cached(value = CACHEKEY + "3")
    public int getParameter(int p) {
        return VALUE1;
    }
    
    @Cached(value = CACHEKEY + "4")
    public int getNamedParameter(@CacheKey int p) {
        return VALUE1 + p;
    }
    
}
