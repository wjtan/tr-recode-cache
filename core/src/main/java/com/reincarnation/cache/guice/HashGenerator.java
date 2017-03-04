package com.reincarnation.cache.guice;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.util.HashFunction;

import com.google.common.base.Strings;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;

/**
 * <p>
 * Description: HashGenerator
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
class HashGenerator {
    private HashGenerator() {
    }
    
    public static int getHashKey(MethodInvocation methodInvocation, String prefix) {
        HashFunction hashFunc = new HashFunction();
        
        if (Strings.isNullOrEmpty(prefix)) {
            hashFunc.hash(methodInvocation.getThis().getClass().getName());
            hashFunc.hash(methodInvocation.getMethod().getName());
        } else {
            hashFunc.hash(prefix);
        }
        
        Object[] arguments = methodInvocation.getArguments();
        
        // Default last element
        boolean hasCacheKeys = false;
        boolean[] isCacheKeys = new boolean[arguments.length];
        Annotation[][] parameterAnnotations = methodInvocation.getMethod().getParameterAnnotations();
        for (int i = 0; i < arguments.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof CacheKey) {
                    isCacheKeys[i] = true;
                    hasCacheKeys = true;
                }
            }
        }
        
        // Write Hash
        for (int i = 0; i < arguments.length; i++) {
            if (hasCacheKeys) {
                if (isCacheKeys[i]) {
                    hashFunc.hash(arguments[i]);
                }
            } else {
                hashFunc.hash(arguments[i]);
            }
        }
        
        return hashFunc.getResult();
    }
}
