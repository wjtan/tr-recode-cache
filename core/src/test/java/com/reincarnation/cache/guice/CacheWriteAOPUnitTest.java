package com.reincarnation.cache.guice;

import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.guice.GuiceInceptorModule;
import com.reincarnation.cache.simple.SimpleCacheModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Description: CacheWriteAOPUnitTest
 * </p>
 * <p>
 * Copyright: 2015
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheWriteAOPUnitTest {
    private static final String CACHEKEY1 = "KeyForCache", CACHEKEY2 = "SomeOtherKey";
    private static final int VALUE1 = 2132, VALUE2 = 5743;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        List<Module> modules = new ArrayList<>();
        modules.add(new SimpleCacheModule());
        modules.add(new GuiceInceptorModule());
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CacheWriteClass.class);
            }
        });
        injector = Guice.createInjector(modules);
    }
    
    public static class CacheWriteClass {
        @CacheWrite(CACHEKEY1)
        public void write1(int value1) {
        }
        
        @Cached(CACHEKEY1)
        public int get1() {
            return -1;
        }
        
        @CacheWrite(CACHEKEY2)
        public void write2(int key, int value) {
        }
        
        @Cached(CACHEKEY2)
        public int get2(int key) {
            return -1;
        }
    }
    
    public static class IllegalCacheClass {
        @CacheWrite(CACHEKEY1)
        public void write() {
        }
    }
    
    @Test
    public void cacheWriteWithNoArguementShouldWork() throws Exception {
        CacheWriteClass clazz = injector.getInstance(CacheWriteClass.class);
        
        // Write cache
        clazz.write1(VALUE1);
        
        // Get from cache
        int actual = clazz.get1();
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWriteWithArguementShouldWork() throws Exception {
        final int key = 999;
        
        CacheWriteClass clazz = injector.getInstance(CacheWriteClass.class);
        
        // Write cache
        clazz.write2(key, VALUE2);
        
        // Get from cache
        int actual = clazz.get2(key);
        
        assertThat(actual).isEqualTo(VALUE2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cacheWriteShouldThrowErrorForNoArgument() throws Exception {
        IllegalCacheClass clazz = injector.getInstance(IllegalCacheClass.class);
        
        clazz.write();
    }
}
