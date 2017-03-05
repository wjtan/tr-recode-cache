package com.reincarnation.cache.caffeine;

import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheValue;
import com.reincarnation.cache.annotation.CacheWrite;
import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.caffeine.CaffeineCacheModule;
import com.reincarnation.cache.guice.GuiceInceptorModule;
import com.reincarnation.cache.util.MockConfigModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Description: CacheAOPUnitTest
 * </p>
 * <p>
 * Copyright: 2015
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CaffeineCacheAOPUnitTest {
    private static final String CACHEKEY = "KeyForCache";
    private static final String CACHEKEY2 = "KeyForCache2";
    private static final String CACHEKEY3 = "KeyForCache3";
    private static final String CACHEKEY4 = "KeyForCache4";
    private static final String CACHEKEY5 = "KeyForCache5";
    private static final String CACHEKEY6_1 = "Key61";
    private static final String CACHEKEY6_2 = "Key62";
    private static final int VALUE1 = 2132;
    private static final int VALUE2 = 456;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        List<Module> modules = new ArrayList<>();
        modules.add(new MockConfigModule());
        modules.add(new CaffeineCacheModule());
        modules.add(new GuiceInceptorModule());
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CacheClass.class);
            }
        });
        
        injector = Guice.createInjector(modules);
    }
    
    @After
    public void clearCache() {
        CacheAdapter adapter = injector.getInstance(CacheAdapter.class);
        adapter.clear();
    }
    
    public static class CacheClass {
        private int value;
        
        public void set(int value) {
            this.value = value;
        }
        
        @Cached
        public int get() {
            return value;
        }
        
        @Cached(CACHEKEY)
        public int get2() {
            return value;
        }
        
        @Cached(CACHEKEY2)
        public int get3(int someArgument) {
            return value;
        }
        
        @CacheWrite(CACHEKEY2)
        public void set3(int someArgument, int value) {
            
        }
        
        /*
         * Time to Live
         */
        @Cached(value = CACHEKEY3, timeToLiveSeconds = 1)
        public int getTimed() {
            return VALUE1;
        }
        
        @CacheWrite(value = CACHEKEY3, timeToLiveSeconds = 1)
        public void setTimed(int value) {
            
        }
        
        /*
         * Nested
         */
        @Cached(timeToLiveSeconds = 1)
        public int getNested1() {
            return value;
        }
        
        @Cached(timeToLiveSeconds = 1)
        public int getNested2() {
            return getNested1();
        }
        
        /*
         * Cache Remove
         */
        
        @CacheWrite(CACHEKEY4)
        public void write4(int key, int value1) {
        }
        
        @Cached(CACHEKEY4)
        public int get4(int key) {
            return VALUE1;
        }
        
        @CacheRemove(CACHEKEY4)
        public void remove4(int key) {
        }
        
        /*
         * Parameter annotation
         */
        @CacheWrite(value = CACHEKEY5)
        public void write5(@CacheValue int value, @CacheKey int key, int someValues) {
        }
        
        @Cached(value = CACHEKEY5)
        public int get5(int somevalues, @CacheKey int key) {
            return VALUE1;
        }
        
        @CacheRemove(value = CACHEKEY5)
        public void remove5(@CacheKey int key, int somevalues) {
        }
        
        @Cached(CACHEKEY6_1)
        public int get6_1() {
            return VALUE1;
        }
        
        @Cached(CACHEKEY6_2)
        public int get6_2() {
            return VALUE1;
        }
        
        @CacheWrite(CACHEKEY6_1)
        public void write6_1(int value) {
        }
        
        @CacheWrite(CACHEKEY6_2)
        public void write6_2(int value) {
        }
        
        @CacheRemove(CACHEKEY6_1)
        @CacheRemove(CACHEKEY6_2)
        public void remove6() {
        }
    }
    
    @Test
    public void cacheWithNoPrefixAndNoArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.set(VALUE1);
        clazz.get();
        
        // Second get is cached
        clazz.set(VALUE2);
        int actual = clazz.get();
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWithPrefixAndNoArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.set(VALUE1);
        clazz.get2();
        
        // Second get is cached
        clazz.set(VALUE2);
        int actual = clazz.get2();
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWriteShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        final int key = 3;
        
        // First get is not cached
        clazz.set(VALUE1);
        clazz.get3(key);
        
        // Second get is cached
        clazz.set(VALUE2);
        assertThat(clazz.get3(key)).isEqualTo(VALUE1);
        
        // Write
        clazz.set3(key, VALUE2);
        
        // 3rd get is new value
        clazz.set(VALUE1);
        assertThat(clazz.get3(key)).isEqualTo(VALUE2);
    }
    
    @Test
    public void temporalCacheShouldWork() throws InterruptedException {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // Write Cache
        clazz.setTimed(VALUE2);
        
        assertThat(clazz.getTimed()).isEqualTo(VALUE2);
        
        Thread.sleep(1001);
        
        assertThat(clazz.getTimed()).isEqualTo(VALUE1);
    }
    
    @Test
    public void nestedGetShouldWork() {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // Cache in first nest
        clazz.set(VALUE1);
        clazz.getNested1();
        
        clazz.set(VALUE2);
        assertThat(clazz.getNested2()).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheRemoveShouldWork() {
        final int key = 5445;
        
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // Write cache
        clazz.write4(key, VALUE2);
        
        // Written value
        assertThat(clazz.get4(key)).isEqualTo(VALUE2);
        
        // Remove cache
        clazz.remove4(key);
        
        // New value from get
        assertThat(clazz.get4(key)).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheRemoveWithParameterAnnotationShouldWork() throws Exception {
        final int key = 5445, otherValue = 345;
        
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // Write cache
        clazz.write5(VALUE2, key, otherValue);
        
        // Written value
        assertThat(clazz.get5(otherValue, key)).isEqualTo(VALUE2);
        
        // Remove cache
        clazz.remove5(key, otherValue);
        
        // New value from get
        assertThat(clazz.get5(otherValue, key)).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheRemoveRepeatableShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // Write cache
        clazz.write6_1(VALUE2);
        clazz.write6_2(VALUE2);
        
        // Written value
        assertThat(clazz.get6_1()).isEqualTo(VALUE2);
        assertThat(clazz.get6_2()).isEqualTo(VALUE2);
        
        // Remove cache
        clazz.remove6();
        
        // New value from get
        assertThat(clazz.get6_1()).isEqualTo(VALUE1);
        assertThat(clazz.get6_2()).isEqualTo(VALUE1);
        
    }
}
