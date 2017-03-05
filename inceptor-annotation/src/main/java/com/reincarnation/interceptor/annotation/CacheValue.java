package com.reincarnation.interceptor.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Description: Label the parameter to be assigned as the CacheValue
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface CacheValue {
    
}
