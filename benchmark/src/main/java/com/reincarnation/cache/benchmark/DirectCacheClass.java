package com.reincarnation.cache.benchmark;

import static com.reincarnation.cache.benchmark.Constants.CACHEKEY;
import static com.reincarnation.cache.benchmark.Constants.VALUE1;

import com.reincarnation.cache.CacheAdapter;

import javax.inject.Inject;

/**
 * <p>
 * Description: DirectCacheClass
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class DirectCacheClass {
    @Inject
    private CacheAdapter cache;
    
    public int directCache(int p) {
        return cache.getOrElse((CACHEKEY + "5" + p).hashCode(), () -> VALUE1 + p);
    }
}
