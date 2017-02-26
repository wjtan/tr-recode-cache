package com.reincarnation.cache.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * <p>
 * Description: CacheWrite
 * </p>
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * public class PaymentService {
 *     &#064;CacheWrite(&quot;MY_KEY&quot;)
 *     public void set(int key) {
 *     }
 * }
 * </pre>
 * <p>
 * Copyright: 2014
 * </p>
 * 
 * @author Denom
 * @version 1.0
 * 
 */
@Retention(RUNTIME)
@Qualifier
@Target(ElementType.METHOD)
public @interface CacheWrite {
    String value();
    
    int timeToLiveSeconds() default 0;
    
    boolean parameterAnnotated() default false;
}
