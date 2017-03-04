package com.reincarnation.cache.enhancer;

import java.util.function.Supplier;

import javax.inject.Singleton;

/**
 * <p>
 * Description: Predicate
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
@Singleton
public class Predicate implements Supplier<Boolean> {
    private boolean value;
    
    public void set(boolean value) {
        this.value = value;
    }
    
    @Override
    public Boolean get() {
        return value;
    }
    
}
