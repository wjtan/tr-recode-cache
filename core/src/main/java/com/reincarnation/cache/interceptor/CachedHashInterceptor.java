package com.reincarnation.cache.interceptor;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.interceptor.annotation.Cache;
import com.reincarnation.interceptor.annotation.CacheDuration;
import com.reincarnation.interceptor.annotation.CachePredicate;
import com.reincarnation.interceptor.annotation.GeneratedHash;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * <p>
 * Description: CachedHashInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public final class CachedHashInterceptor {
    private CachedHashInterceptor() {
    }
    
    @BindingPriority(1)
    @RuntimeType
    public static Object intercept(@Cache CacheAdapter cache,
                                   @GeneratedHash int hash,
                                   @SuperCall Callable<?> callable)
            throws Exception {
        if (cache != null) {
            return cache.getOrElse(hash, callable);
        } else {
            return callable.call();
        }
    }
    
    @BindingPriority(2)
    @RuntimeType
    public static Object intercept(@Cache CacheAdapter cache,
                                   @GeneratedHash int hash,
                                   @CacheDuration int duration,
                                   @SuperCall Callable<?> callable)
            throws Exception {
        if (cache != null) {
            return cache.getOrElse(hash, callable, duration);
        } else {
            return callable.call();
        }
    }
    
    @BindingPriority(3)
    @RuntimeType
    public static Object intercept(@Cache CacheAdapter cache,
                                   @CachePredicate Supplier<Boolean> predicate,
                                   @GeneratedHash int hash,
                                   @SuperCall Callable<?> callable)
            throws Exception {
        
        // If true
        if (cache != null && predicate.get()) {
            return cache.getOrElse(hash, callable);
        } else {
            return callable.call();
        }
    }
    
    @BindingPriority(4)
    @RuntimeType
    public static Object intercept(@Cache CacheAdapter cache,
                                   @CachePredicate Supplier<Boolean> predicate,
                                   @GeneratedHash int hash,
                                   @CacheDuration int duration,
                                   @SuperCall Callable<?> callable)
            throws Exception {
        // If true
        if (cache != null && predicate.get()) {
            return cache.getOrElse(hash, callable, duration);
        } else {
            return callable.call();
        }
    }
}
