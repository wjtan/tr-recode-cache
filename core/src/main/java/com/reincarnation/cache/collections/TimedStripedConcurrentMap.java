package com.reincarnation.cache.collections;

import com.reincarnation.cache.caffeine.TimedObject;

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
public class TimedStripedConcurrentMap<K, V> {
    private final Striped<ReadWriteLock> rwLockStripes;
    private final Map<K, TimedObject<V>> baseMap;
    private final int timeToLiveSeconds;
    
    public TimedStripedConcurrentMap(int striped, Map<K, TimedObject<V>> underlayingMap, int timeToLiveSeconds) {
        this.rwLockStripes = Striped.readWriteLock(striped);
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.baseMap = underlayingMap;
    }
    
    public V get(K key, Function<K, V> function) {
        ReadWriteLock rwLock = rwLockStripes.get(key);
        rwLock.readLock().lock();
        // If key does not exist or expired
        if (!baseMap.containsKey(key) || baseMap.get(key).isExpired(timeToLiveSeconds)) {
            // Must release read lock before acquiring write lock
            rwLock.readLock().unlock();
            rwLock.writeLock().lock();
            try {
                // Recheck state because another thread might have
                // acquired write lock and changed state before we did.
                if (!baseMap.containsKey(key) || baseMap.get(key).isExpired(timeToLiveSeconds)) {
                    V value = function.apply(key);
                    baseMap.put(key, new TimedObject<>(value));
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
            return baseMap.get(key).getValue();
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
