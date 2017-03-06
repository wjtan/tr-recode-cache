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
    
    /**
     * Returns the value associated with the {@code key} in this cache, obtaining that value from the
     * {@code callable} if necessary.
     */
    <T> T getOrElse(int key, Callable<T> callable) throws Exception;
    
    /**
     * Returns the value associated with the {@code key} and {@code timeToLiveInSeconds} in this cache, obtaining that value from
     * the {@code callable} if necessary or when the value is expired.
     */
    <T> T getOrElse(int key, Callable<T> callable, int timeToLiveInSeconds) throws Exception;
    
    /**
     * Associates the {@code value} with the {@code key} in this cache. If the cache previously
     * contained a value associated with the {@code key}, the old value is replaced by the new
     * {@code value}.
     */
    void put(int hash, Object value);
    
    /**
     * Associates the {@code value} with the {@code key} and {@code timeToLiveInSeconds} in this cache. If the cache previously
     * contained a value associated with the {@code key}, the old value is replaced by the new {@code value}.
     */
    void put(int hash, Object value, int timeToLiveInSeconds);
    
    /**
     * Discards any cached value for the {@code key}.
     */
    void remove(int key);
    
    /**
     * Remove or invalidate all entries
     */
    void clear();
    
    /**
     * Discards any cached value for the key generated from {@code prefix} and {@code args}.
     */
    default void remove(String prefix, Object... args) {
        HashFunction hashFunc = new HashFunction(prefix.hashCode());
        for (Object arg : args) {
            hashFunc.hash(arg);
        }
        int hash = hashFunc.getResult();
        remove(hash);
    }
}
