package com.reincarnation.cache.interceptor;

import com.reincarnation.cache.ThreadLocalCacheAdapter;
import com.reincarnation.interceptor.annotation.GeneratedHash;
import com.reincarnation.interceptor.annotation.ThreadLocalCache;

import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * <p>
 * Description: ThreadLocalCachedHashInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public final class ThreadLocalCachedHashInterceptor {
    private ThreadLocalCachedHashInterceptor() {
    }
    
    @RuntimeType
    public static Object intercept(@ThreadLocalCache ThreadLocalCacheAdapter cache,
                                   @GeneratedHash int hash,
                                   @SuperCall Callable<?> callable)
            throws Exception {
        if (cache != null) {
            return cache.getOrElse(hash, callable);
        } else {
            return callable.call();
        }
    }
}
