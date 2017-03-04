package com.reincarnation.cache.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.reincarnation.cache.util.AlwaysTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Supplier;

import javax.inject.Qualifier;

/**
 * <p>
 * Description: Cached
 * </p>
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * public class PaymentService {
 *     &#064;Cached
 *     public int get(int key) {
 *     }
 * 
 *     &#064;Cached(&quot;MY_KEY&quot;)
 *     public int getOther(int key) {
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
public @interface Cached {
    String value() default "";
    
    int timeToLiveSeconds() default 0;
    
    /**
     * If predicate return False, cache will be disabled
     */
    Class<? extends Supplier<Boolean>> predicate() default AlwaysTrue.class;
}
