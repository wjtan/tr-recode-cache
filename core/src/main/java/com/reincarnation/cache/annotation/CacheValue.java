package com.reincarnation.cache.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * <p>
 * Description: Cache Value
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 * 
 * @author Denom
 * @version 1.0
 * 
 */

@Retention(RUNTIME)
@Qualifier
@Target(ElementType.PARAMETER)
public @interface CacheValue {
}
