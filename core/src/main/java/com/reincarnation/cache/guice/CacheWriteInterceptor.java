package com.reincarnation.cache.guice;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.CacheValue;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.util.HashFunction;

import com.google.common.base.Strings;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

/**
 * <p>
 * Description: CacheWriteInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
class CacheWriteInterceptor implements MethodInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheWriteInterceptor.class);
    
    @Inject
    private CacheAdapter cache;
    
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation.getMethod().isAnnotationPresent(CacheWrite.class)) {
            CacheWrite cacheAnnotation = methodInvocation.getMethod().getAnnotation(CacheWrite.class);
            writeCache(methodInvocation, cacheAnnotation);
            return methodInvocation.proceed();
        } else {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have a CacheWrite annotation.");
        }
    }
    
    protected void writeCache(MethodInvocation methodInvocation, CacheWrite cacheAnnotation) {
        LOGGER.trace("Cache Write Through");
        
        Object[] arguments = methodInvocation.getArguments();
        if (arguments.length == 0) {
            throw new IllegalArgumentException("Method " + methodInvocation.getMethod().getName() + " does not have an argument for caching");
        }
        
        HashFunction hashFunc = new HashFunction();
        
        // Hash primary key
        String prefix = cacheAnnotation.value();
        if (Strings.isNullOrEmpty(prefix)) {
            hashFunc.hash(methodInvocation.getThis().getClass().getName());
            hashFunc.hash(methodInvocation.getMethod().getName());
        } else {
            hashFunc.hash(prefix);
        }
        
        // Default last element
        int resultIndex = arguments.length - 1;
        boolean hasCacheKeys = false;
        boolean[] isCacheKeys = new boolean[arguments.length];
        Annotation[][] parameterAnnotations = methodInvocation.getMethod().getParameterAnnotations();
        for (int i = 0; i < arguments.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof CacheValue) {
                    resultIndex = i;
                } else if (annotation instanceof CacheKey) {
                    isCacheKeys[i] = true;
                    hasCacheKeys = true;
                }
            }
        }
        
        LOGGER.trace("Result at index {}", resultIndex);
        
        // Write Hash
        for (int i = 0; i < arguments.length; i++) {
            // Skip cache value
            if (resultIndex == i) {
                continue;
            }
            
            if (hasCacheKeys) {
                if (isCacheKeys[i]) {
                    hashFunc.hash(arguments[i]);
                }
            } else {
                hashFunc.hash(arguments[i]);
            }
        }
        
        int hash = hashFunc.getResult();
        int ttl = cacheAnnotation.timeToLiveSeconds();
        Object result = arguments[resultIndex];
        
        LOGGER.trace("Writing cache: {} -> {}, ttl={}", hash, result, ttl);
        if (ttl == 0) {
            cache.put(hash, result);
        } else {
            cache.put(hash, result, ttl);
        }
    }
    
}
