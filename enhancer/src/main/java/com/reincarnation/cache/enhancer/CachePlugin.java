package com.reincarnation.cache.enhancer;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.annotation.IgnoreCacheEnhancer;
import com.reincarnation.cache.enhancer.binder.CachePredicateBinder;
import com.reincarnation.cache.enhancer.binder.CacheRemoveHashBinder;
import com.reincarnation.cache.enhancer.binder.CacheValueBinder;
import com.reincarnation.cache.enhancer.binder.CacheWriteDurationBinder;
import com.reincarnation.cache.enhancer.binder.CacheWriteHashBinder;
import com.reincarnation.cache.enhancer.binder.CachedDurationBinder;
import com.reincarnation.cache.enhancer.binder.CachedHashBinder;
import com.reincarnation.cache.inceptor.CacheRemoveInterceptor;
import com.reincarnation.cache.inceptor.CacheWriteInterceptor;
import com.reincarnation.cache.inceptor.CachedHashInterceptor;
import com.reincarnation.cache.util.AlwaysTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
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
                    annotation.getAnnotationType().represents(CacheRemoves.class)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Builder<?> apply(Builder<?> builder, TypeDescription target) {
        Builder<?> builder2 = builder.defineField("cache", CacheAdapter.class, Visibility.PUBLIC).annotateField(new InjectImpl());
        builder2 = applyPredicate(builder2, target);
        
        // More detailed match lower
        return builder2.method(not(isDeclaredBy(Object.class)).and(isAnnotatedWith(CacheRemove.class).or(isAnnotatedWith(CacheRemoves.class))).and(not(isStatic())))
                       .intercept(MethodDelegation.withDefaultConfiguration()
                                                  .withBinders(CacheRemoveHashBinder.INSTANCE)
                                                  .to(CacheRemoveInterceptor.class))
                       
                       .method(not(isDeclaredBy(Object.class)).and(isAnnotatedWith(CacheWrite.class)).and(not(isStatic())))
                       .intercept(MethodDelegation.withDefaultConfiguration()
                                                  .withBinders(CacheWriteHashBinder.INSTANCE,
                                                               CacheWriteDurationBinder.INSTANCE,
                                                               CacheValueBinder.INSTANCE)
                                                  .to(CacheWriteInterceptor.class))
                       
                       .method(not(isDeclaredBy(Object.class)).and(isAnnotatedWith(Cached.class)).and(not(isStatic())))
                       .intercept(MethodDelegation.withDefaultConfiguration()
                                                  .withBinders(CachedHashBinder.INSTANCE,
                                                               CachedDurationBinder.INSTANCE,
                                                               CachePredicateBinder.INSTANCE)
                                                  .to(CachedHashInterceptor.class));
    }
    
    protected static String name(String prefix) {
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
            Cached cached = sourceAnnotation.prepare(Cached.class).loadSilent();
            
            if (cached.predicate().equals(AlwaysTrue.class)) {
                continue;
            }
            
            TypeDescription predicateType = new TypeDescription.ForLoadedType(cached.predicate());
            
            if (variables.containsKey(predicateType)) {
                LOGGER.trace("Already added Predicate: {}", predicateType);
                continue;
            }
            
            String variableName = name(predicateType.getSimpleName());
            
            LOGGER.trace("Add Predicate: {} -> {}", predicateType, variableName);
            variables.put(predicateType, variableName);
            builder2 = builder2.defineField(variableName, predicateType, Visibility.PUBLIC).annotateField(new InjectImpl());
            
        }
        
        return builder2;
    }
    
}
