package com.reincarnation.cache.guice;

import com.reincarnation.cache.CacheException;

import org.aopalliance.intercept.MethodInvocation;

import java.util.concurrent.Callable;

/**
 * <p>
 * Description: Utils
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
final class Utils {
    public static Callable<Object> invocationToCallable(MethodInvocation methodInvocation) {
        return () -> {
            try {
                return methodInvocation.proceed();
            } catch (Throwable e) { // NOSONAR
                throw new CacheException(e);
            }
        };
    }
}
