package com.reincarnation.cache.enhancer;

import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.caffeine.CaffeineCacheModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * Description: CacheWriteTest
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheWriteTest {
    private static final int VALUE1 = 123;
    private static final int KEY = 1234567890;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        Class<? extends CacheWriteObject> clazz = new CacheInceptor().intercept(CacheWriteObject.class);
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new MockConfigModule());
                install(new CaffeineCacheModule());
                
                bind(CacheWriteObject.class).to(clazz);
                
            }
        });
    }
    
    @Test
    public void writeShouldWork() {
        CacheWriteObject test = injector.getInstance(CacheWriteObject.class);
        
        test.write(VALUE1);
        
        assertThat(test.getCached()).isEqualTo(VALUE1);
    }
    
    @Test
    public void writeKeyShouldWork() {
        CacheWriteObject test = injector.getInstance(CacheWriteObject.class);
        
        test.writeKey(KEY, VALUE1);
        
        assertThat(test.getCachedKey(KEY)).isEqualTo(VALUE1);
    }
    
    @Test
    public void writeValueShouldWork() {
        CacheWriteObject test = injector.getInstance(CacheWriteObject.class);
        
        test.writeValue(VALUE1, KEY);
        
        assertThat(test.getCachedKey(KEY)).isEqualTo(VALUE1);
    }
    
    @Test
    public void writeTimedShouldWork() {
        CacheWriteObject test = injector.getInstance(CacheWriteObject.class);
        
        test.writeTimed(VALUE1);
        
        assertThat(test.getTimed()).isEqualTo(VALUE1);
    }
}
