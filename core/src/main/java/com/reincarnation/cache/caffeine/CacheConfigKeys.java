package com.reincarnation.cache.caffeine;

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
    
    public static final String CAFFEINE_BASE = BASE + "caffeine.";
    public static final String CAFFEINE_STATISTICS = BASE + "statistics";
    public static final String CAFFEINE_SIZE = CAFFEINE_BASE + "size";
    
    private CacheConfigKeys() {
    }
}
