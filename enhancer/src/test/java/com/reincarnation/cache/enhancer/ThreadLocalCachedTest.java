package com.reincarnation.cache.enhancer;

import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.ThreadLocalCacheAdapter;
import com.reincarnation.cache.caffeine.CaffeineCacheModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * Description: ThreadLocalCachedTest
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public class ThreadLocalCachedTest {
    private static final int KEY = 25345;
    private static final int VALUE1 = 123;
    private static final int VALUE2 = 321;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        Class<? extends ThreadLocalCachedObject> clazz = new CacheInterceptor().intercept(ThreadLocalCachedObject.class);
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new MockConfigModule());
                install(new CaffeineCacheModule());
                
                bind(ThreadLocalCachedObject.class).to(clazz);
                
            }
        });
    }
    
    @Test
    public void getWithoutCacheShouldWork() {
        ThreadLocalCachedObject test = injector.getInstance(ThreadLocalCachedObject.class);
        
        // Caching first value
        test.set(VALUE1);
        assertThat(test.get(KEY)).isEqualTo(VALUE1);
        
        // Should be updated
        test.set(VALUE2);
        assertThat(test.get(KEY)).isEqualTo(VALUE2);
    }
    
    @Test
    public void getWithCacheShouldWork() {
        ThreadLocalCacheAdapter cache = injector.getInstance(ThreadLocalCacheAdapter.class);
        
        ThreadLocalCachedObject test = injector.getInstance(ThreadLocalCachedObject.class);
        
        cache.start();
        
        // Caching first value
        test.set(VALUE1);
        assertThat(test.get(KEY)).isEqualTo(VALUE1);
        
        // Should be still cached
        test.set(VALUE2);
        assertThat(test.get(KEY)).isEqualTo(VALUE1);
        
        cache.end();
    }
}
