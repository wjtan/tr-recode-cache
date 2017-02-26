package com.reincarnation.cache;

import com.github.benmanes.caffeine.cache.Cache;

import org.aopalliance.intercept.MethodInvocation;

import java.util.concurrent.Callable;

/**
 * <p>
 * Description: ErrorWrapper
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public final class ErrorWrapper {
    private ErrorWrapper() {
    }
    
    public static Object get(MethodInvocation methodInvocation) {
        try {
            return methodInvocation.proceed();
        } catch (Throwable e) { // NOSONAR
            throw new CacheException(e);
        }
    }
    
    public static <T> T get(Callable<T> block) {
        try {
            return block.call();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
    
    public static Throwable unwrap(CacheException ex) {
        Throwable cause = ex.getCause();
        while (cause instanceof CacheException) {
            cause = cause.getCause();
        }
        return cause;
    }
    
    /*
     * public static <K, V> V getFromCache(Cache<K, V> cache, K key, Function<K, V> function) throws Throwable {
     * try {
     * return cache.get(key, key2 -> {
     * try {
     * return function.apply(key2);
     * } catch (Exception e) {
     * throw new CacheException(e);
     * }
     * });
     * } catch (CacheException ex) {
     * // Unwrap and throw exception
     * throw ErrorWrapper.unwrap(ex);
     * }
     * }
     */
}
