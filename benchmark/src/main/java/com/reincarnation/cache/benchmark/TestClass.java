package com.reincarnation.cache.benchmark;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * <p>
 * Description: TestClass
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class TestClass {
    private Map<Integer, Object> cache = new HashMap<>();
    private Cache<Integer, Object> caffeine = Caffeine.newBuilder().build();
    
    public int getValue(int parameter) {
        return parameter;
    }
    
    public int getCached(int parameter) {
        return (Integer) cache.computeIfAbsent(parameter, key -> getValue(parameter));
    }
    
    public int getCachedCallable(int parameter) {
        Callable<Integer> call = () -> getValue(parameter);
        return (Integer) cache.computeIfAbsent(parameter, key -> {
            try {
                return call.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public int getCaffeine(int parameter) {
        return (Integer) caffeine.get(parameter, key -> getValue(parameter));
    }
    
    public int getCaffeineCallable(int parameter) {
        Callable<Integer> call = () -> getValue(parameter);
        return (Integer) caffeine.get(parameter, key -> {
            try {
                return call.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public int getCaffeineIntercept(int parameter) {
        Callable<Integer> call = () -> getValue(parameter);
        return (Integer) intercept(caffeine, parameter, call);
    }
    
    public static Object intercept(Cache<Integer, Object> cache,
                                   int hash,
                                   Callable<?> callable) {
        // System.out.println("Hash:" + hash);
        return cache.get(hash, hash2 -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
