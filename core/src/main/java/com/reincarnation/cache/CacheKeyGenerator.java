package com.reincarnation.cache;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheValue;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;

import com.google.common.base.Strings;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Description: KeyGenerator
 * </p>
 * 
 * @author Denom
 * 
 */
public final class CacheKeyGenerator {
    private CacheKeyGenerator() {
    }
    
    public static String getCacheKey(MethodInvocation methodInvocation, Cached cacheAnnotation) {
        String prefix = cacheAnnotation.value();
        if (Strings.isNullOrEmpty(prefix)) {
            prefix = getDefaultKeyName(methodInvocation);
        }
        StringBuilder key = new StringBuilder();
        key.append(prefix);
        if (cacheAnnotation.parameterAnnotated()) {
            key.append(getKeyFromArguments(getCacheKeyArguments(methodInvocation)));
        } else {
            key.append(getKeyFromArguments(methodInvocation.getArguments()));
        }
        return key.toString();
    }
    
    public static String getCacheKey(MethodInvocation methodInvocation, CacheWrite cacheAnnotation) {
        String prefix = cacheAnnotation.value();
        
        StringBuilder key = new StringBuilder();
        key.append(prefix);
        if (cacheAnnotation.parameterAnnotated()) {
            key.append(getKeyFromArguments(getCacheKeyArguments(methodInvocation)));
        } else {
            key.append(getWriteKeyFromArguments(methodInvocation.getArguments()));
        }
        return key.toString();
    }
    
    public static String getCacheKey(MethodInvocation methodInvocation, CacheRemove cacheAnnotation) {
        String prefix = cacheAnnotation.value();
        
        StringBuilder key = new StringBuilder();
        key.append(prefix);
        if (cacheAnnotation.parameterAnnotated()) {
            key.append(getKeyFromArguments(getCacheKeyArguments(methodInvocation)));
        } else {
            key.append(getKeyFromArguments(methodInvocation.getArguments()));
        }
        return key.toString();
    }
    
    /*
     * Cache key for @Cached
     */
    public static String getCacheKey(String prefix, Object... args) {
        StringBuilder key = new StringBuilder();
        key.append(prefix);
        key.append(getKeyFromArguments(args));
        return key.toString();
    }
    
    /*
     * Get key from arguments
     */
    protected static String getKeyFromArguments(Object[] arguments) {
        return getKeyFromArguments(arguments, arguments.length);
    }
    
    protected static String getKeyFromArguments(Object[] arguments, int length) {
        StringBuilder key = new StringBuilder();
        key.append("(");
        if (length > 0) {
            key.append(getKey(arguments[0]));
        }
        for (int i = 1; i < length; i++) {
            key.append(", ").append(getKey(arguments[i]));
        }
        key.append(")");
        return key.toString();
    }
    
    protected static String getKeyFromArguments(List<Object> arguments) {
        StringBuilder key = new StringBuilder();
        key.append("(");
        boolean first = true;
        for (Object argument : arguments) {
            if (first) {
                first = false;
            } else {
                key.append(", ");
            }
            key.append(getKey(argument));
        }
        key.append(")");
        return key.toString();
    }
    
    /*
     * Last argument should be the write object
     */
    protected static String getWriteKeyFromArguments(Object[] arguments) {
        return getKeyFromArguments(arguments, arguments.length - 1);
    }
    
    protected static String getDefaultKeyName(MethodInvocation methodInvocation) {
        StringBuilder key = new StringBuilder();
        key.append(methodInvocation.getThis().getClass().getName())
           .append("#")
           .append(methodInvocation.getMethod().getName());
        return key.toString();
    }
    
    protected static String getKey(Object keyObject) {
        try {
            StringBuilder key = new StringBuilder();
            key.append(keyObject.getClass().getSimpleName()).append(":");
            if (keyObject instanceof Object[]) {
                key.append(Arrays.deepToString((Object[]) keyObject));
            } else if (keyObject instanceof Collection<?>) {
                key.append(Arrays.deepToString(((Collection<?>) keyObject).toArray()));
            } else {
                key.append("[").append(keyObject).append("]");
            }
            return key.toString();
        } catch (NullPointerException e) { // NOSONAR
            return "";
        }
    }
    
    /*
     * Parse cache key
     */
    protected static List<Object> getCacheKeyArguments(MethodInvocation methodInvocation) {
        List<Object> selectedArguments = new ArrayList<>();
        Annotation[][] parameterAnnotations = methodInvocation.getMethod().getParameterAnnotations();
        Object[] arguments = methodInvocation.getArguments();
        int i = 0;
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof CacheKey) {
                    selectedArguments.add(arguments[i]);
                }
            }
            i += 1;
        }
        return selectedArguments;
    }
    
    public static Object getCacheValue(MethodInvocation methodInvocation) {
        Annotation[][] parameterAnnotations = methodInvocation.getMethod().getParameterAnnotations();
        Object[] arguments = methodInvocation.getArguments();
        int i = 0;
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof CacheValue) {
                    return arguments[i];
                }
            }
            i += 1;
        }
        
        throw new IllegalArgumentException("No argument annotated with CacheValue");
    }
}
