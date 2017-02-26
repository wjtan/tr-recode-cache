package com.reincarnation.cache;

import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;

import com.google.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;

/**
 * <p>
 * Description: CacheInterceptor
 * </p>
 * <p>
 * Copyright: 2014
 * </p>
 * 
 * @author Denom
 * @version 1.0
 * @version 1.1 Changed default write object to last argument.
 * 
 */
class CacheInterceptor implements MethodInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheInterceptor.class);
    
    @Inject
    public CacheAdapter cache;
    
    // @Inject
    // private Provider<ArmageddonService> armageddonProvider;
    
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation.getMethod().isAnnotationPresent(Cached.class)) {
            Cached cacheAnnotation = methodInvocation.getMethod().getAnnotation(Cached.class);
            
            // Disable in armageddon
            // if (cacheAnnotation.disableInArmageddon() && armageddonProvider.get().hasStarted()) {
            // return methodInvocation.proceed();
            // }
            
            String cacheKey = CacheKeyGenerator.getCacheKey(methodInvocation, cacheAnnotation);
            try {
                return cache.getOrElse(cacheKey, () -> {
                    LOGGER.trace("Unable to find element in cache.  Calling method to calculate value...");
                    return ErrorWrapper.get(methodInvocation);
                }, cacheAnnotation.timeToLiveSeconds());
            } catch (CacheException ex) {
                throw ErrorWrapper.unwrap(ex);
            }
        } else if (methodInvocation.getMethod().isAnnotationPresent(CacheWrite.class)) {
            CacheWrite cacheAnnotation = methodInvocation.getMethod().getAnnotation(CacheWrite.class);
            
            String cacheKey = CacheKeyGenerator.getCacheKey(methodInvocation, cacheAnnotation);
            int ttl = cacheAnnotation.timeToLiveSeconds();
            
            if (cacheAnnotation.parameterAnnotated()) {
                writeCacheFromCacheValue(methodInvocation, cacheKey, ttl);
            } else {
                writeCache(methodInvocation, cacheKey, ttl);
            }
            return methodInvocation.proceed();
        } else if (methodInvocation.getMethod().isAnnotationPresent(CacheRemove.class)) {
            CacheRemove cacheAnnotation = methodInvocation.getMethod().getAnnotation(CacheRemove.class);
            String cacheKey = CacheKeyGenerator.getCacheKey(methodInvocation, cacheAnnotation);
            cache.remove(cacheKey);
            return methodInvocation.proceed();
        } else if (methodInvocation.getMethod().isAnnotationPresent(CacheRemoves.class)) {
            CacheRemove[] cacheAnnotations = methodInvocation.getMethod().getAnnotationsByType(CacheRemove.class);
            for (CacheRemove cacheAnnotation : cacheAnnotations) {
                String cacheKey = CacheKeyGenerator.getCacheKey(methodInvocation, cacheAnnotation);
                cache.remove(cacheKey);
            }
            return methodInvocation.proceed();
        } else {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have a "
                                               + Cached.class.getSimpleName() + "or"
                                               + CacheWrite.class.getSimpleName() + " annotation.");
        }
    }
    
    void writeCache(MethodInvocation methodInvocation, String cacheKey, int ttl) {
        LOGGER.trace("Cache Write Through");
        Object[] argument = methodInvocation.getArguments();
        if (argument.length > 0) {
            Object result = argument[argument.length - 1];
            LOGGER.trace("Putting result in cache: {}", result);
            cache.put(cacheKey, result, ttl);
        } else {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName()
                                               + " does not have an argument for caching");
        }
    }
    
    void writeCacheFromCacheValue(MethodInvocation methodInvocation, String cacheKey, int ttl) {
        Object result = CacheKeyGenerator.getCacheValue(methodInvocation);
        cache.put(cacheKey, result, ttl);
    }
    
}
