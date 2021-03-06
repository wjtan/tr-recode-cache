package com.reincarnation.interceptor.annotation;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Description: Label parameter as the Hash value
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(PARAMETER)
public @interface GeneratedHash {
    
}
