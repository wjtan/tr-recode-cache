package com.reincarnation.cache.guice;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * <p>
 * Description: CacheRemoveInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
class CacheRemoveInterceptor implements MethodInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheRemoveInterceptor.class);
    
    @Inject
    private CacheAdapter cache;
    
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation.getMethod().isAnnotationPresent(CacheRemove.class)) {
            CacheRemove cacheAnnotation = methodInvocation.getMethod().getAnnotation(CacheRemove.class);
            removeCache(methodInvocation, cacheAnnotation);
            return methodInvocation.proceed();
        } else if (methodInvocation.getMethod().isAnnotationPresent(CacheRemoves.class)) {
            CacheRemove[] cacheAnnotations = methodInvocation.getMethod().getAnnotationsByType(CacheRemove.class);
            for (CacheRemove cacheAnnotation : cacheAnnotations) {
                removeCache(methodInvocation, cacheAnnotation);
            }
            return methodInvocation.proceed();
        } else {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have a CacheRemove annotation.");
        }
        
    }
    
    private void removeCache(MethodInvocation methodInvocation, CacheRemove cacheAnnotation) {
        int hash = HashGenerator.getHashKey(methodInvocation, cacheAnnotation.value());
        LOGGER.trace("Remove Cache with hash={}", hash);
        cache.remove(hash);
    }
}
