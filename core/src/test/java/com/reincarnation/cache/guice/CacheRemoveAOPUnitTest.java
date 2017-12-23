package com.reincarnation.cache.guice;

import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheValue;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.guice.GuiceInterceptorModule;
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
public class CacheRemoveAOPUnitTest {
    private static final String CACHEKEY1 = "KeyForCache";
    private static final String CACHEKEY2 = "SomeOtherKey";
    private static final String CACHEKEY3 = "MoreKey";
    private static final String CACHEKEY4_1 = "Key41";
    private static final String CACHEKEY4_2 = "Key42";
    private static final int VALUE1 = 2132;
    private static final int VALUE2 = 5743;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        List<Module> modules = new ArrayList<>();
        modules.add(new SimpleCacheModule());
        modules.add(new GuiceInterceptorModule());
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CacheRemoveClass.class);
            }
        });
        injector = Guice.createInjector(modules);
    }
    
    public static class CacheRemoveClass {
        @CacheWrite(CACHEKEY1)
        public void write1(int value1) {
        }
        
        @Cached(CACHEKEY1)
        public int get1() {
            return VALUE1;
        }
        
        @CacheRemove(CACHEKEY1)
        public void remove1() {
        }
        
        @CacheWrite(CACHEKEY2)
        public void write2(int key, int value1) {
        }
        
        @Cached(CACHEKEY2)
        public int get2(int key) {
            return VALUE1;
        }
        
        @CacheRemove(CACHEKEY2)
        public void remove2(int key) {
        }
        
        @CacheWrite(value = CACHEKEY3)
        public void write3(@CacheValue int value, @CacheKey int key, int someValues) {
        }
        
        @Cached(value = CACHEKEY3)
        public int get3(int somevalues, @CacheKey int key) {
            return VALUE1;
        }
        
        @CacheRemove(value = CACHEKEY3)
        public void remove3(@CacheKey int key, int somevalues) {
        }
        
        /*
         * CacheRemove repeatable
         */
        @Cached(CACHEKEY4_1)
        public int get4_1() {
            return VALUE1;
        }
        
        @Cached(CACHEKEY4_2)
        public int get4_2() {
            return VALUE1;
        }
        
        @CacheWrite(CACHEKEY4_1)
        public void write4_1(int value) {
        }
        
        @CacheWrite(CACHEKEY4_2)
        public void write4_2(int value) {
        }
        
        @CacheRemove(CACHEKEY4_1)
        @CacheRemove(CACHEKEY4_2)
        public void remove4() {
        }
    }
    
    @Test
    public void cacheRemoveWithNoArguementShouldWork() throws Exception {
        CacheRemoveClass clazz = injector.getInstance(CacheRemoveClass.class);
        
        // Write cache
        clazz.write1(VALUE2);
        
        // Written value
        assertThat(clazz.get1()).isEqualTo(VALUE2);
        
        // Remove cache
        clazz.remove1();
        
        // New value from get
        assertThat(clazz.get1()).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheRemoveWithArguementShouldWork() throws Exception {
        final int key = 5445;
        
        CacheRemoveClass clazz = injector.getInstance(CacheRemoveClass.class);
        
        // Write cache
        clazz.write2(key, VALUE2);
        
        // Written value
        assertThat(clazz.get2(key)).isEqualTo(VALUE2);
        
        // Remove cache
        clazz.remove2(key);
        
        // New value from get
        assertThat(clazz.get2(key)).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheRemoveWithParameterAnnotationShouldWork() throws Exception {
        final int key = 5445, otherValue = 345;
        
        CacheRemoveClass clazz = injector.getInstance(CacheRemoveClass.class);
        
        // Write cache
        clazz.write3(VALUE2, key, otherValue);
        
        // Written value
        assertThat(clazz.get3(otherValue, key)).isEqualTo(VALUE2);
        
        // Remove cache
        clazz.remove3(key, otherValue);
        
        // New value from get
        assertThat(clazz.get3(otherValue, key)).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheRemoveRepeatableShouldWork() throws Exception {
        CacheRemoveClass clazz = injector.getInstance(CacheRemoveClass.class);
        
        // Write cache
        clazz.write4_1(VALUE2);
        clazz.write4_2(VALUE2);
        
        // Written value
        assertThat(clazz.get4_1()).isEqualTo(VALUE2);
        assertThat(clazz.get4_2()).isEqualTo(VALUE2);
        
        // Remove cache
        clazz.remove4();
        
        // New value from get
        assertThat(clazz.get4_1()).isEqualTo(VALUE1);
        assertThat(clazz.get4_2()).isEqualTo(VALUE1);
        
    }
}
