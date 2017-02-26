package com.reincarnation.cache;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * <p>
 * Description: CacheAdapterRemover
 * </p>
 * <p>
 * Copyright: 2015
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
@Singleton
class CacheAdapterRemover implements CacheRemover {
    private final CacheAdapter cache;
    
    @Inject
    public CacheAdapterRemover(CacheAdapter cache) {
        this.cache = cache;
    }
    
    @Override
    public void remove(String prefix, Object... args) {
        String key = CacheKeyGenerator.getCacheKey(prefix, args);
        
        cache.remove(key);
    }
}
