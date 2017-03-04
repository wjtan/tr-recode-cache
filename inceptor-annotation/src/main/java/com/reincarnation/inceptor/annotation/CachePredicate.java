package com.reincarnation.inceptor.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Description: CachePredicate
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface CachePredicate {
    
}
