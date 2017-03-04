package com.reincarnation.cache;

import com.reincarnation.cache.util.HashFunction;

import java.util.concurrent.Callable;

/**
 * <p>
 * Description: CacheAdapter
 * </p>
 * <p>
 * Copyright: 2014
 * </p>
 * 
 * @author Denom
 * @version 1.0
 * 
 */
public interface CacheAdapter {
    
    <T> T getOrElse(int hash, Callable<T> callable);
    
    <T> T getOrElse(int hash, Callable<T> callable, int timeToLiveInSeconds);
    
    void put(int hash, Object value);
    
    void put(int hash, Object value, int timeToLiveInSeconds);
    
    void remove(int hash);
    
    default void remove(String prefix, Object... args) {
        HashFunction hashFunc = new HashFunction(prefix.hashCode());
        for (Object arg : args) {
            hashFunc.hash(arg);
        }
        int hash = hashFunc.getResult();
        remove(hash);
    }
}
