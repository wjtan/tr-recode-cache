package com.reincarnation.cache.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Description: IgnoreCacheEnhancer
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface IgnoreCacheEnhancer {
    
}
