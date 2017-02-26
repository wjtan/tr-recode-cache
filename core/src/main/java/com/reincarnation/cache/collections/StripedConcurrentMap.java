package com.reincarnation.cache.collections;

import com.google.common.util.concurrent.Striped;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;

/**
 * <p>
 * Description: StripedConcurrentMap
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class StripedConcurrentMap<K, V> {
    private final Striped<ReadWriteLock> rwLockStripes;
    private final Map<K, V> baseMap;
    
    public StripedConcurrentMap(int striped, Map<K, V> baseMap) {
        this.rwLockStripes = Striped.readWriteLock(striped);
        this.baseMap = baseMap;
    }
    
    public V get(K key, Function<K, V> function) {
        ReadWriteLock rwLock = rwLockStripes.get(key);
        rwLock.readLock().lock();
        if (!baseMap.containsKey(key)) {
            // Must release read lock before acquiring write lock
            rwLock.readLock().unlock();
            rwLock.writeLock().lock();
            try {
                // Recheck state because another thread might have
                // acquired write lock and changed state before we did.
                if (!baseMap.containsKey(key)) {
                    V value = function.apply(key);
                    baseMap.put(key, value);
                    return value;
                }
                
                // Downgrade by acquiring read lock before releasing write lock
                rwLock.readLock().lock();
            } finally {
                // Unlock write, still hold read
                rwLock.writeLock().unlock();
            }
        }
        
        try {
            return baseMap.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
