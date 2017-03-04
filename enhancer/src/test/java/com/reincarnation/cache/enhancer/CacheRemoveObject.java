package com.reincarnation.cache.enhancer;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.Cached;

/**
 * <p>
 * Description: CacheRemoveObject
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheRemoveObject {
    public static final String KEY = "RemoveKey";
    public static final String KEY2 = "RemoveKey";
    
    private int value;
    private int value2;
    
    public void set(int value) {
        this.value = value;
    }
    
    public void set2(int value) {
        this.value2 = value;
    }
    
    @CacheRemove(KEY)
    public void remove() {
        
    }
    
    @Cached(KEY)
    public int get() {
        return value;
    }
    
    @CacheRemove(KEY)
    public void removeKey(int key) {
        
    }
    
    @Cached(KEY)
    public int get(int key) {
        return value;
    }
    
    @CacheRemove(KEY)
    public void removeKey(int value, @CacheKey int key) {
        
    }
    
    @Cached(KEY)
    public int get2() {
        return value2;
    }
    
    @CacheRemove(KEY)
    @CacheRemove(KEY2)
    public void removeMultiple() {
        
    }
}
