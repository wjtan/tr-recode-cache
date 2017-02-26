package com.reincarnation.cache.caffeine;

import static com.reincarnation.cache.caffeine.CaffeineCacheNames.STATIC_CACHE;
import static com.reincarnation.cache.caffeine.CaffeineCacheNames.TEMPORAL_CACHE;

import com.reincarnation.cache.CacheException;
import com.reincarnation.cache.CacheKeyGenerator;
import com.reincarnation.cache.ErrorWrapper;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.util.concurrent.Striped;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * <p>
 * Description: CaffeineCacheInterceptor
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 * 
 * @author Denom
 * 
 */
class CaffeineCacheInterceptor implements MethodInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaffeineCacheInterceptor.class);
    private static final int NUMBER_STRIPES = 10;
    
    @Inject
    @Named(STATIC_CACHE)
    private Cache<String, Object> staticCache;
    
    @Inject
    @Named(TEMPORAL_CACHE)
    private Cache<String, TimedObject<Object>> temporalCache;
    
    // @Inject
    // private Provider<ArmageddonService> armageddonProvider;
    
    private Striped<Lock> locks = Striped.lazyWeakLock(NUMBER_STRIPES);
    
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
                return cacheGet(cacheKey, methodInvocation, cacheAnnotation);
            } catch (CacheException ex) {
                // Unwrap and throw exception
                throw ErrorWrapper.unwrap(ex);
            }
        } else if (methodInvocation.getMethod().isAnnotationPresent(CacheWrite.class)) {
            CacheWrite cacheAnnotation = methodInvocation.getMethod().getAnnotation(CacheWrite.class);
            writeCache(methodInvocation, cacheAnnotation);
            return methodInvocation.proceed();
        } else if (methodInvocation.getMethod().isAnnotationPresent(CacheRemove.class)) {
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
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have a "
                                               + Cached.class.getSimpleName() + "or"
                                               + CacheWrite.class.getSimpleName() + " annotation.");
        }
        
    }
    
    Object cacheGet(String cacheKey, MethodInvocation methodInvocation, Cached cacheAnnotation) throws Throwable { // NOSONAR
        // If no time to live, use static cache
        if (cacheAnnotation.timeToLiveSeconds() == 0) {
            return staticCacheGet(cacheKey, methodInvocation);
        } else {
            return temporalCacheGet(cacheKey, methodInvocation, cacheAnnotation.timeToLiveSeconds());
        }
    }
    
    Object staticCacheGet(String cacheKey, MethodInvocation methodInvocation) throws Throwable { // NOSONAR
        Object result = staticCache.getIfPresent(cacheKey);
        if (result != null) {
            return result;
        }
        
        LOGGER.trace("Static Cache: {}", cacheKey);
        
        // Non-atomic update for nested @Cached
        Object newResult = methodInvocation.proceed();
        staticCache.put(cacheKey, newResult);
        return newResult;
    }
    
    // Use lock per cache key to ensure value is updated atomically
    Object temporalCacheGet(String cacheKey, MethodInvocation methodInvocation, int timeToLiveSeconds) throws Throwable { // NOSONAR
        TimedObject<Object> result = temporalCache.getIfPresent(cacheKey);
        if (result != null && !result.isExpired(timeToLiveSeconds)) {
            return result.getValue();
        }
        
        Lock lock = locks.get(cacheKey);
        try {
            lock.lock();
            
            // Check again
            TimedObject<Object> result2 = temporalCache.getIfPresent(cacheKey);
            if (result2 != null && !result2.isExpired(timeToLiveSeconds)) {
                return result2.getValue();
            }
            
            LOGGER.trace("Temporal Cache: {}", cacheKey);
            
            Object newResult = methodInvocation.proceed();
            temporalCache.put(cacheKey, new TimedObject<>(newResult));
            return newResult;
        } finally {
            lock.unlock();
        }
    }
    
    void writeCache(MethodInvocation methodInvocation, CacheWrite cacheAnnotation) {
        LOGGER.trace("Cache Write Through");
        
        String cacheKey = CacheKeyGenerator.getCacheKey(methodInvocation, cacheAnnotation);
        
        Object[] argument = methodInvocation.getArguments();
        if (argument.length == 0) {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have an argument for caching");
        }
        
        Object result;
        if (cacheAnnotation.parameterAnnotated()) {
            result = CacheKeyGenerator.getCacheValue(methodInvocation);
        } else {
            result = argument[argument.length - 1];
        }
        
        LOGGER.trace("Putting result in cache: {}", result);
        if (cacheAnnotation.timeToLiveSeconds() == 0) {
            staticCache.put(cacheKey, result);
        } else {
            temporalCache.put(cacheKey, new TimedObject<>(result));
        }
    }
    
    void removeCache(MethodInvocation methodInvocation, CacheRemove cacheAnnotation) {
        String cacheKey = CacheKeyGenerator.getCacheKey(methodInvocation, cacheAnnotation);
        staticCache.invalidate(cacheKey);
        temporalCache.invalidate(cacheKey);
    }
    
}
