package com.reincarnation.cache.enhancer;

import java.io.IOException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;

/**
 * <p>
 * Description: CacheInterceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheInterceptor {
    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> intercept(Class<? extends T> clazz) {
        try (CachePlugin plugin = new CachePlugin()) {
            
            TypeDescription target = new TypeDescription.ForLoadedType(clazz);
            if (!plugin.matches(target)) {
                return clazz;
            }
            
            Builder<? extends T> builder = new ByteBuddy().subclass(clazz);
            
            ClassLoader classLoader = getClass().getClassLoader();
            ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.of(classLoader);
            
            return ((Builder<? extends T>) plugin.apply(builder, target, classFileLocator)).make()
                                                                                           .load(classLoader)
                                                                                           .getLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
