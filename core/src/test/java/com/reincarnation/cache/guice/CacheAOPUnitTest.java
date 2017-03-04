package com.reincarnation.cache.guice;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.annotation.Cached;
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
 * Description: CacheAOPUnitTest
 * </p>
 * <p>
 * Copyright: 2015
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CacheAOPUnitTest {
    private static final String CACHEKEY = "KeyForCache";
    private static final int VALUE1 = 2132;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        List<Module> modules = new ArrayList<>();
        modules.add(new SimpleCacheModule());
        modules.add(new GuiceInceptorModule());
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CacheClass.class);
            }
        });
        
        injector = Guice.createInjector(modules);
    }
    
    public static class CacheClass {
        @Cached
        public int get1() {
            return VALUE1;
        }
        
        @Cached(CACHEKEY)
        public int get2() {
            return VALUE1;
        }
        
        @Cached
        public int get3(int someArgument) {
            return VALUE1;
        }
        
        @Cached(CACHEKEY)
        public int get4(int[] someArgument, List<Integer> moreArugments) {
            return VALUE1;
        }
    }
    
    @Test
    public void cacheWithNoPrefixAndNoArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.get1();
        
        // Second get is cached
        int actual = clazz.get1();
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWithPrefixAndNoArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.get2();
        
        // Second get is cached
        int actual = clazz.get2();
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWithNoPrefixAndArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.get3(3);
        
        // Second get is cached
        int actual = clazz.get3(3);
        
        assertThat(actual).isEqualTo(VALUE1);
    }
    
    @Test
    public void cacheWithPrefixAndArguementShouldWork() throws Exception {
        CacheClass clazz = injector.getInstance(CacheClass.class);
        
        // First get is not cached
        clazz.get4(new int[] { 4 }, asList(5));
        
        // Second get is cached
        int actual = clazz.get4(new int[] { 4 }, asList(5));
        
        assertThat(actual).isEqualTo(VALUE1);
    }
}
