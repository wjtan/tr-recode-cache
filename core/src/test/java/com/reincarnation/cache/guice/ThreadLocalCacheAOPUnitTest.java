package com.reincarnation.cache.guice;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.ThreadLocalCacheAdapter;
import com.reincarnation.cache.annotation.ThreadLocalCached;
import com.reincarnation.cache.guice.GuiceInterceptorModule;
import com.reincarnation.cache.simple.SimpleCacheModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Description: ThreadLocalCacheAOPUnitTest
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class ThreadLocalCacheAOPUnitTest {
    static final String CACHEKEY = "KeyForCache";
    private static final int VALUE1 = 2132;
    private static final int VALUE2 = 5678;
    
    private static Injector injector;
    private static ThreadLocalCacheAdapter cache;
    
    @BeforeClass
    public static void startApp() throws Exception {
        List<Module> modules = new ArrayList<>();
        modules.add(new SimpleCacheModule());
        modules.add(new GuiceInterceptorModule());
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CacheClass.class);
            }
        });
        
        injector = Guice.createInjector(modules);
        cache = injector.getInstance(ThreadLocalCacheAdapter.class);
    }
    
    public static class CacheClass {
        int value;
        
        @ThreadLocalCached
        public int get1() {
            return value;
        }
        
        @ThreadLocalCached(ThreadLocalCacheAOPUnitTest.CACHEKEY)
        public int get2() {
            return value;
        }
        
        @ThreadLocalCached
        public int get3(int someArgument) {
            return value;
        }
        
        @ThreadLocalCached(ThreadLocalCacheAOPUnitTest.CACHEKEY)
        public int get4(List<Integer> moreArugments) {
            return value;
        }
    }
    
    @Before
    public void start() {
        cache.start();
    }
    
    @After
    public void end() {
        cache.end();
    }
    
    @Test
    public void cacheWithNoPrefixAndNoArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.value = VALUE1;
        clazz.get1();
        
        // Second get is cached
        clazz.value = VALUE2;
        int actual = clazz.get1();
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWithPrefixAndNoArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.value = VALUE1;
        clazz.get2();
        
        // Second get is cached
        clazz.value = VALUE2;
        int actual = clazz.get2();
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWithNoPrefixAndArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.value = VALUE1;
        clazz.get3(3);
        
        // Second get is cached
        clazz.value = VALUE2;
        int actual = clazz.get3(3);
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWithPrefixAndArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.value = VALUE1;
        clazz.get4(asList(5));
        
        // Second get is cached
        clazz.value = VALUE2;
        int actual = clazz.get4(asList(5));
        
        assertThat(actual).isEqualTo(VALUE1);
    }
}
