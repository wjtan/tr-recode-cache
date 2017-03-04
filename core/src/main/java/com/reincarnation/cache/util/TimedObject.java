package com.reincarnation.cache.util;

import java.time.Instant;

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
    private final Instant expiry;
    private final T value;
    
    public TimedObject(T value, int timeToLiveSeconds) {
        this.expiry = Instant.now().plusSeconds(timeToLiveSeconds);
        this.value = value;
    }
    
    public boolean isExpired() {
        return expiry.isBefore(Instant.now());
    }
    
    public T getValue() {
        return value;
    }
}
