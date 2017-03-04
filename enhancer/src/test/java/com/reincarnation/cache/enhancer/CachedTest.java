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
 * Description: App
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public class CachedTest {
    private static final int KEY = 25345;
    private static final int VALUE1 = 123;
    private static final int VALUE2 = 321;
    
    private static Injector injector;
    
    @BeforeClass
    public static void startApp() throws Exception {
        Class<? extends CachedObject> clazz = new CacheInceptor().intercept(CachedObject.class);
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new MockConfigModule());
                install(new CaffeineCacheModule());
                
                bind(Predicate.class);
                bind(CachedObject.class).to(clazz);
                
            }
        });
    }
    
    @Test
    public void getShouldWork() {
        CachedObject test = injector.getInstance(CachedObject.class);
        
        // Caching first value
        test.set(VALUE1);
        assertThat(test.get(KEY)).isEqualTo(VALUE1);
        
        // Should be still cached
        test.set(VALUE2);
        assertThat(test.get(KEY)).isEqualTo(VALUE1);
    }
    
    @Test
    public void getTimedShouldWork() throws InterruptedException {
        CachedObject test = injector.getInstance(CachedObject.class);
        
        // Caching first value
        test.set(VALUE1);
        assertThat(test.getTimed(KEY)).isEqualTo(VALUE1);
        
        Thread.sleep(1005);
        
        // Caching 2nd value
        test.set(VALUE2);
        assertThat(test.getTimed(KEY)).isEqualTo(VALUE2);
    }
    
    @Test
    public void getPredicateShouldWork() throws InterruptedException {
        CachedObject test = injector.getInstance(CachedObject.class);
        Predicate predicate = injector.getInstance(Predicate.class);
        
        // Caching first value
        test.set(VALUE1);
        predicate.set(true);
        assertThat(test.getPredicate(KEY)).isEqualTo(VALUE1);
        
        // Caching 2nd value
        test.set(VALUE2);
        predicate.set(false); // Disable cache
        assertThat(test.getPredicate(KEY)).isEqualTo(VALUE2);
    }
    
    @Test
    public void getPredicateTimedShouldWork() throws InterruptedException {
        CachedObject test = injector.getInstance(CachedObject.class);
        Predicate predicate = injector.getInstance(Predicate.class);
        predicate.set(true);
        
        // Caching first value
        test.set(VALUE1);
        assertThat(test.getPredicateTimed(KEY)).isEqualTo(VALUE1);
        
        Thread.sleep(1005);
        
        // Caching 2nd value
        test.set(VALUE2);
        assertThat(test.getPredicateTimed(KEY)).isEqualTo(VALUE2);
    }
}
