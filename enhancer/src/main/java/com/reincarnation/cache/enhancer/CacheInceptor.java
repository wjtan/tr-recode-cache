package com.reincarnation.cache.enhancer;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;

/**
 * <p>
 * Description: CacheInceptor
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheInceptor {
    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> intercept(Class<? extends T> clazz) {
        CachePlugin plugin = new CachePlugin();
        
        TypeDescription target = new TypeDescription.ForLoadedType(clazz);
        if (!plugin.matches(target)) {
            return clazz;
        }
        
        Builder<? extends T> builder = new ByteBuddy().subclass(clazz);
        
        return ((Builder<? extends T>) plugin.apply(builder, target)).make()
                                                                     .load(getClass().getClassLoader())
                                                                     .getLoaded();
    }
}
