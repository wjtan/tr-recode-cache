package com.reincarnation.cache.impl;

import com.reincarnation.cache.ThreadLocalCacheAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Singleton;

/**
 * <p>
 * Description: ThreadLocalCacheAdapterImpl
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author twj
 * @version 1.0
 */
@Singleton
class ThreadLocalCacheAdapterImpl implements ThreadLocalCacheAdapter {
    private static class Context {
        boolean enable = false;
        Map<Integer, Object> cache;
    }
    
    private static final ThreadLocal<Context> CONTEXTS = ThreadLocal.withInitial(Context::new);
    
    @Override
    public void start() {
        CONTEXTS.get().enable = true;
    }
    
    @Override
    public void end() {
        CONTEXTS.remove();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrElse(int key, Callable<T> callable) throws Exception {
        Context context = CONTEXTS.get();
        if (context.enable) {
            Map<Integer, Object> cache = context.cache;
            
            // Only initialize map when used
            if (cache == null) {
                cache = new HashMap<>();
                context.cache = cache;
            }
            
            T value = (T) cache.get(key);
            if (value != null) {
                return value;
            }
            
            T value2 = callable.call();
            cache.put(key, value2);
            return value2;
        }
        return callable.call();
    }
    
}
