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
 * Description: CacheRemoveTest
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheRemoveTest {
    private static final int VALUE1 = 123;
    private static final int VALUE2 = 321;
    private static final int KEY = 1234567890;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        Class<? extends CacheRemoveObject> clazz = new CacheInceptor().intercept(CacheRemoveObject.class);
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new MockConfigModule());
                install(new CaffeineCacheModule());
                
                bind(CacheRemoveObject.class).to(clazz);
                
            }
        });
    }
    
    @Test
    public void removeTest() {
        CacheRemoveObject test = injector.getInstance(CacheRemoveObject.class);
        
        // Caching
        test.set(VALUE1);
        test.get();
        
        test.remove();
        
        test.set(VALUE2);
        assertThat(test.get()).isEqualTo(VALUE2);
    }
    
    @Test
    public void removeKeyTest() {
        CacheRemoveObject test = injector.getInstance(CacheRemoveObject.class);
        
        // Caching
        test.set(VALUE1);
        test.get(KEY);
        
        test.removeKey(KEY);
        
        test.set(VALUE2);
        assertThat(test.get(KEY)).isEqualTo(VALUE2);
    }
    
    @Test
    public void removeKey2Test() {
        CacheRemoveObject test = injector.getInstance(CacheRemoveObject.class);
        
        // Caching
        test.set(VALUE1);
        test.get(KEY);
        
        test.removeKey(VALUE1, KEY);
        
        test.set(VALUE2);
        assertThat(test.get(KEY)).isEqualTo(VALUE2);
    }
    
    @Test
    public void removeMultipleTest() {
        CacheRemoveObject test = injector.getInstance(CacheRemoveObject.class);
        
        // Caching
        test.set(VALUE1);
        test.get();
        
        test.set2(VALUE1);
        test.get2();
        
        test.removeMultiple();
        
        test.set(VALUE2);
        assertThat(test.get()).isEqualTo(VALUE2);
        
        test.set2(VALUE2);
        assertThat(test.get2()).isEqualTo(VALUE2);
    }
    
}
