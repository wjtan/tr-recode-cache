package com.reincarnation.cache.caffeine;

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
    private final Instant timestamp;
    private final T value;
    
    public TimedObject(T value) {
        this.timestamp = Instant.now();
        this.value = value;
    }
    
    public boolean isExpired(int timeToLiveSeconds) {
        return timestamp.plusSeconds(timeToLiveSeconds).isBefore(Instant.now());
    }
    
    public T getValue() {
        return value;
    }
}
