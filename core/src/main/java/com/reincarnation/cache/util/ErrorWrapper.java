package com.reincarnation.cache.util;

import com.reincarnation.cache.CacheException;

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
    
    public static Callable<Object> invocationToCallable(MethodInvocation methodInvocation) {
        return () -> {
            try {
                return methodInvocation.proceed();
            } catch (Throwable e) { // NOSONAR
                throw new CacheException(e);
            }
        };
    }
    
    public static <T> T get(Callable<T> block) {
        try {
            return block.call();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
    
    public static <T> TimedObject<T> getTimedObject(Callable<T> block, int timeToLiveInSeconds) {
        try {
            return new TimedObject<>(block.call(), timeToLiveInSeconds);
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
    
}
