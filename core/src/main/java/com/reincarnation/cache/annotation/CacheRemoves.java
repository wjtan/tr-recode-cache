package com.reincarnation.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * <p>
 * Description: CacheRemoves
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 * 
 * @author Denom
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target(ElementType.METHOD)
public @interface CacheRemoves {
    CacheRemove[] value() default {};
}
