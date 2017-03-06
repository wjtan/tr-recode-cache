package com.reincarnation.cache.interceptor;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.interceptor.annotation.CacheDuration;
import com.reincarnation.interceptor.annotation.CacheValue;
import com.reincarnation.interceptor.annotation.GeneratedHash;

import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * <p>
 * Description: CacheWriteInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public final class CacheWriteInterceptor {
    private CacheWriteInterceptor() {
    }
    
    @RuntimeType
    public static Object interceptPut(@FieldValue(value = "cache") CacheAdapter cache,
                                      @GeneratedHash int hash,
                                      @CacheValue Object value,
                                      @SuperCall Callable<?> callable)
            throws Exception {
        
        if (cache != null) {
            cache.put(hash, value);
        }
        return callable.call();
    }
    
    @RuntimeType
    public static Object interceptPut(@FieldValue(value = "cache") CacheAdapter cache,
                                      @GeneratedHash int hash,
                                      @CacheDuration int ttl,
                                      @CacheValue Object value,
                                      @SuperCall Callable<?> callable)
            throws Exception {
        
        if (cache != null) {
            cache.put(hash, value, ttl);
        }
        return callable.call();
    }
    
}
