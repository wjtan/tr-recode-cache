package com.reincarnation.cache;

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
    
    public <T> T getOrElse(String key, Callable<T> block);
    
    public <T> T getOrElse(String key, Callable<T> block, int timeToLiveInSeconds);
    
    public void put(String key, Object value);
    
    public void put(String key, Object value, int timeToLiveInSeconds);
    
    public void remove(String key);
}
