package com.reincarnation.cache.enhancer;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.ThreadLocalCacheAdapter;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.annotation.IgnoreCacheEnhancer;
import com.reincarnation.cache.annotation.ThreadLocalCached;
import com.reincarnation.cache.enhancer.annotation.IgnoreCacheEnhancerImpl;
import com.reincarnation.cache.enhancer.annotation.InjectImpl;
import com.reincarnation.cache.enhancer.binder.CacheBinder;
import com.reincarnation.cache.enhancer.binder.CachePredicateBinder;
import com.reincarnation.cache.enhancer.binder.CacheRemoveHashBinder;
import com.reincarnation.cache.enhancer.binder.CacheValueBinder;
import com.reincarnation.cache.enhancer.binder.CacheWriteDurationBinder;
import com.reincarnation.cache.enhancer.binder.CacheWriteHashBinder;
import com.reincarnation.cache.enhancer.binder.CachedDurationBinder;
import com.reincarnation.cache.enhancer.binder.CachedHashBinder;
import com.reincarnation.cache.enhancer.binder.ThreadLocalCacheBinder;
import com.reincarnation.cache.interceptor.CacheRemoveInterceptor;
import com.reincarnation.cache.interceptor.CacheWriteInterceptor;
import com.reincarnation.cache.interceptor.CachedHashInterceptor;
import com.reincarnation.cache.interceptor.ThreadLocalCachedHashInterceptor;
import com.reincarnation.cache.util.AlwaysTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.RandomString;

/**
 * <p>
 * Description: CachePlugin
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CachePlugin implements Plugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachePlugin.class);
    
    private static final RandomString RANDOM_STRING = new RandomString();
    
    @Override
    public boolean matches(TypeDescription target) {
        if (target.getDeclaredAnnotations().isAnnotationPresent(IgnoreCacheEnhancer.class)) {
            return false;
        }
        
        for (MethodDescription method : target.getDeclaredMethods()) {
            for (AnnotationDescription annotation : method.getDeclaredAnnotations()) {
                if (annotation.getAnnotationType().represents(Cached.class) ||
                    annotation.getAnnotationType().represents(CacheWrite.class) ||
                    annotation.getAnnotationType().represents(CacheRemove.class) ||
                    annotation.getAnnotationType().represents(CacheRemoves.class) ||
                    annotation.getAnnotationType().represents(ThreadLocalCached.class)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Builder<?> apply(Builder<?> builder, TypeDescription target, ClassFileLocator classFileLocator) {
        boolean hasCached = false;
        boolean hasCacheWrite = false;
        boolean hasCacheRemove = false;
        boolean hasThreadLocalCache = false;
        
        for (MethodDescription method : target.getDeclaredMethods()) {
            for (AnnotationDescription annotation : method.getDeclaredAnnotations()) {
                hasCached |= annotation.getAnnotationType().represents(Cached.class);
                hasCacheWrite |= annotation.getAnnotationType().represents(CacheWrite.class);
                hasCacheRemove |= annotation.getAnnotationType().represents(CacheRemove.class);
                hasCacheRemove |= annotation.getAnnotationType().represents(CacheRemoves.class);
                hasThreadLocalCache |= annotation.getAnnotationType().represents(ThreadLocalCached.class);
            }
        }
        
        // Add IgnoreCacheEnhancer so that class will not be enhanced again
        Builder<?> builder2 = builder.annotateType(new IgnoreCacheEnhancerImpl());
        
        if (hasCached || hasCacheWrite || hasCacheRemove) {
            builder2 = builder2.defineField(randomName("cache"), CacheAdapter.class, Visibility.PROTECTED).annotateField(new InjectImpl());
        }
        
        if (hasThreadLocalCache) {
            builder2 = builder2.defineField(randomName("localCache"), ThreadLocalCacheAdapter.class, Visibility.PROTECTED).annotateField(new InjectImpl());
        }
        
        if (hasCached) {
            builder2 = applyPredicate(builder2, target);
        }
        
        // More detailed match lower
        if (hasCacheRemove) {
            builder2 = builder2.method(not(isDeclaredBy(Object.class)).and(isAnnotatedWith(CacheRemove.class).or(isAnnotatedWith(CacheRemoves.class))).and(not(isStatic())))
                               .intercept(MethodDelegation.withDefaultConfiguration()
                                                          .withBinders(CacheBinder.INSTANCE,
                                                                       CacheRemoveHashBinder.INSTANCE)
                                                          .to(CacheRemoveInterceptor.class));
        }
        
        if (hasCacheWrite) {
            builder2 = builder2.method(not(isDeclaredBy(Object.class)).and(isAnnotatedWith(CacheWrite.class)).and(not(isStatic())))
                               .intercept(MethodDelegation.withDefaultConfiguration()
                                                          .withBinders(CacheBinder.INSTANCE,
                                                                       CacheWriteHashBinder.INSTANCE,
                                                                       CacheWriteDurationBinder.INSTANCE,
                                                                       CacheValueBinder.INSTANCE)
                                                          .to(CacheWriteInterceptor.class));
        }
        
        if (hasCached) {
            builder2 = builder2.method(not(isDeclaredBy(Object.class)).and(isAnnotatedWith(Cached.class)).and(not(isStatic())))
                               .intercept(MethodDelegation.withDefaultConfiguration()
                                                          .withBinders(CacheBinder.INSTANCE,
                                                                       CachedHashBinder.INSTANCE,
                                                                       CachedDurationBinder.INSTANCE,
                                                                       CachePredicateBinder.INSTANCE)
                                                          .to(CachedHashInterceptor.class));
        }
        
        if (hasThreadLocalCache) {
            builder2 = builder2.method(not(isDeclaredBy(Object.class)).and(isAnnotatedWith(ThreadLocalCached.class)).and(not(isStatic())))
                               .intercept(MethodDelegation.withDefaultConfiguration()
                                                          .withBinders(ThreadLocalCacheBinder.INSTANCE,
                                                                       CachedHashBinder.INSTANCE)
                                                          .to(ThreadLocalCachedHashInterceptor.class));
        }
        
        return builder2;
    }
    
    protected static String randomName(String prefix) {
        return String.format("%s$%s", prefix, RANDOM_STRING.nextString());
    }
    
    /**
     * Add predicate as Injectable variables
     */
    public <T> Builder<T> applyPredicate(Builder<T> builder, TypeDescription target) {
        Builder<T> builder2 = builder;
        
        Map<TypeDescription, String> variables = new HashMap<>();
        
        for (MethodDescription method : target.getDeclaredMethods()) {
            if (method.isStatic()) {
                continue;
            }
            
            if (!method.getDeclaredAnnotations().isAnnotationPresent(Cached.class)) {
                continue;
            }
            
            AnnotationDescription sourceAnnotation = method.getDeclaredAnnotations().ofType(Cached.class);
            Cached cached = sourceAnnotation.prepare(Cached.class).load();
            
            if (cached.predicate().equals(AlwaysTrue.class)) {
                continue;
            }
            
            TypeDescription predicateType = new TypeDescription.ForLoadedType(cached.predicate());
            
            if (variables.containsKey(predicateType)) {
                LOGGER.trace("Already added Predicate: {}", predicateType);
                continue;
            }
            
            String variableName = randomName(predicateType.getSimpleName());
            
            LOGGER.trace("Add Predicate: {} -> {}", predicateType, variableName);
            variables.put(predicateType, variableName);
            builder2 = builder2.defineField(variableName, predicateType, Visibility.PROTECTED).annotateField(new InjectImpl());
            
        }
        
        return builder2;
    }
    
    @Override
    public void close() throws IOException {
        
    }
    
}
