package com.reincarnation.cache.interceptor;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.interceptor.annotation.Cache;
import com.reincarnation.interceptor.annotation.GeneratedHash;

import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * <p>
 * Description: CacheRemoveInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public final class CacheRemoveInterceptor {
    private CacheRemoveInterceptor() {
    }
    
    @RuntimeType
    public static Object interceptRemove(@Cache CacheAdapter cache,
                                         @GeneratedHash int hash,
                                         @SuperCall Callable<?> callable)
            throws Exception {
        
        if (cache != null) {
            cache.remove(hash);
        }
        return callable.call();
    }
    
    @RuntimeType
    public static Object interceptRemoves(@Cache CacheAdapter cache,
                                          @GeneratedHash int[] hashes,
                                          @SuperCall Callable<?> callable)
            throws Exception {
        
        if (cache != null) {
            for (int hash : hashes) {
                cache.remove(hash);
            }
        }
        return callable.call();
    }
}
