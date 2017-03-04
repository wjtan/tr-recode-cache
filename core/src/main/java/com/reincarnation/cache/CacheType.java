package com.reincarnation.cache;

/**
 * <p>
 * Description: CacheType
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public enum CacheType {
    CONCURRENT_HASHMAP,
    CAFFEINE;
    
    public static final String CACHE_TYPE = "cache.type";
}
