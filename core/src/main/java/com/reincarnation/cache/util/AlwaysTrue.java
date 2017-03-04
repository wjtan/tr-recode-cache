package com.reincarnation.cache.util;

import java.util.function.Supplier;

/**
 * <p>
 * Description: AlwaysTrue
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public class AlwaysTrue implements Supplier<Boolean> {
    
    @Override
    public Boolean get() {
        return true;
    }
    
}
