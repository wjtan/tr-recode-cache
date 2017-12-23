package com.reincarnation.cache.guice;

import com.reincarnation.cache.ThreadLocalCacheAdapter;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.annotation.ThreadLocalCached;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * <p>
 * Description: ThreadLocalCachedInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
class ThreadLocalCachedInterceptor implements MethodInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalCachedInterceptor.class);
    
    @Inject
    private ThreadLocalCacheAdapter cache;
    
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation.getMethod().isAnnotationPresent(ThreadLocalCached.class)) {
            ThreadLocalCached cacheAnnotation = methodInvocation.getMethod().getAnnotation(ThreadLocalCached.class);
            
            int hash = HashGenerator.getHashKey(methodInvocation, cacheAnnotation.value());
            
            LOGGER.trace("Caching with hash={}", hash);
            
            return cache.getOrElse(hash, Utils.invocationToCallable(methodInvocation));
        } else {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have a "
                                               + Cached.class.getSimpleName() + " annotation.");
        }
    }
    
}
