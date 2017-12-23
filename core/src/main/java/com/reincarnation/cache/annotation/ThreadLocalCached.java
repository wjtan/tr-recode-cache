package com.reincarnation.cache.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * <p>
 * Description: ThreadLocalCached - Cache for current thread
 * </p>
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * public class PaymentService {
 *     &#064;ThreadLocalCached
 *     public int get(int key) {
 *     }
 * 
 *     &#064;ThreadLocalCached(&quot;MY_KEY&quot;)
 *     public int getOther(int key) {
 *     }
 * }
 * </pre>
 * 
 * @author Denom
 */

@Retention(RUNTIME)
@Qualifier
@Target(ElementType.METHOD)
public @interface ThreadLocalCached {
    String value() default "";
}
