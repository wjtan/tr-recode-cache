package com.reincarnation.cache.guice;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.util.AlwaysTrue;
import com.reincarnation.cache.util.ErrorWrapper;

import com.google.inject.Injector;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import javax.inject.Inject;

/**
 * <p>
 * Description: CachedInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
class CachedInterceptor implements MethodInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedInterceptor.class);
    
    @Inject
    private CacheAdapter cache;
    
    @Inject
    private Injector injector;
    
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation.getMethod().isAnnotationPresent(Cached.class)) {
            Cached cacheAnnotation = methodInvocation.getMethod().getAnnotation(Cached.class);
            
            Class<? extends Supplier<Boolean>> predicateClass = cacheAnnotation.predicate();
            if (!predicateClass.equals(AlwaysTrue.class)) {
                Supplier<Boolean> predicate = injector.getInstance(predicateClass);
                if (!predicate.get()) {
                    LOGGER.trace("Predicate Failed, proceed without Cache");
                    return methodInvocation.proceed();
                }
            }
            
            int hash = HashGenerator.getHashKey(methodInvocation, cacheAnnotation.value());
            int timeToLive = cacheAnnotation.timeToLiveSeconds();
            
            LOGGER.trace("Caching with hash={}, ttl={}", hash, timeToLive);
            if (timeToLive == 0) {
                return cache.getOrElse(hash, ErrorWrapper.invocationToCallable(methodInvocation));
            } else {
                return cache.getOrElse(hash, ErrorWrapper.invocationToCallable(methodInvocation), timeToLive);
            }
        } else {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have a "
                                               + Cached.class.getSimpleName() + " annotation.");
        }
    }
}
