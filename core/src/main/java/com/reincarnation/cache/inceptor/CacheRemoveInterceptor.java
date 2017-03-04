package com.reincarnation.cache.inceptor;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.inceptor.annotation.GeneratedHash;

import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.FieldValue;
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
    public static Object interceptRemove(@FieldValue(value = "cache") CacheAdapter cache,
                                         @GeneratedHash int hash,
                                         @SuperCall Callable<?> callable)
            throws Exception {
        
        cache.remove(hash);
        return callable.call();
    }
    
    @RuntimeType
    public static Object interceptRemoves(@FieldValue(value = "cache") CacheAdapter cache,
                                          @GeneratedHash int[] hashes,
                                          @SuperCall Callable<?> callable)
            throws Exception {
        
        for (int hash : hashes) {
            cache.remove(hash);
        }
        return callable.call();
    }
}