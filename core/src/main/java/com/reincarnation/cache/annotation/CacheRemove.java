package com.reincarnation.cache.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * <p>
 * Description: CacheRemove
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
@Repeatable(CacheRemoves.class)
@Target(ElementType.METHOD)
public @interface CacheRemove {
    String value() default "";
    
    boolean parameterAnnotated() default false;
}
