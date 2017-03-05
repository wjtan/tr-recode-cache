package com.reincarnation.cache.util;

/**
 * <p>
 * Description: TimedObject
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class TimedObject<T> {
    private final long expiry;
    private final T value;
    
    public TimedObject(T value, int timeToLiveSeconds) {
        this.expiry = System.currentTimeMillis() + 60 * timeToLiveSeconds;
        this.value = value;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiry;
    }
    
    public T getValue() {
        return value;
    }
}
