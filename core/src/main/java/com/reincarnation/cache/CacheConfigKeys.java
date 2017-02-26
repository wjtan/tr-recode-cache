package com.reincarnation.cache;

/**
 * <p>
 * Description: CacheConfigKeys
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public final class CacheConfigKeys {
    public static final String BASE = "application.cache.";
    public static final String STATISTICS = BASE + "statistics";
    
    public static final String CAFFEINE_BASE = BASE + "caffeine.";
    public static final String CAFFEINE_STATIC_SIZE = CAFFEINE_BASE + "static.size";
    public static final String CAFFEINE_TEMPORAL_SIZE = CAFFEINE_BASE + "temporal.size";
    
    private CacheConfigKeys() {
    }
}
