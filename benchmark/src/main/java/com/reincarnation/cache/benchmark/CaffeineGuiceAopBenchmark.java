package com.reincarnation.cache.benchmark;

import com.reincarnation.cache.caffeine.CaffeineCacheModule;
import com.reincarnation.cache.guice.GuiceInceptorModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Description: CaffineCacheBenchmark
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CaffeineGuiceAopBenchmark {
    
    @State(Scope.Benchmark)
    public static class ExecutionState {
        private Injector injector;
        
        @Setup(Level.Trial)
        public void doSetup() {
            List<Module> modules = new ArrayList<>();
            modules.add(new MockConfigModule());
            modules.add(new CaffeineCacheModule());
            modules.add(new GuiceInceptorModule());
            modules.add(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CacheClass.class);
                    bind(DirectCacheClass.class);
                }
            });
            
            injector = Guice.createInjector(modules);
            
        }
        
        @Setup(Level.Iteration)
        public void inject() {
            cacheClass = injector.getInstance(CacheClass2.class);
            directCacheClass = injector.getInstance(DirectCacheClass.class);
        }
        
        public CacheClass2 cacheClass;
        public DirectCacheClass directCacheClass;
    }
    
    // Static Cache
    @Benchmark
    public int cache(ExecutionState state) {
        return state.cacheClass.get();
    }
    
    // Static Cache with key
    @Benchmark
    public int cacheWithKey(ExecutionState state) {
        return state.cacheClass.getKey();
    }
    
    // Temporal Cache
    @Benchmark
    public int temporalCache(ExecutionState state) {
        return state.cacheClass.getTimed();
    }
    
    @Benchmark
    public int temporalCacheWithKey(ExecutionState state) {
        return state.cacheClass.getKeyTimed();
    }
    
    @Benchmark
    public int cacheParameter(ExecutionState state) {
        return state.cacheClass.getParameter(5);
    }
    
    @Benchmark
    public int cacheNamedParameter(ExecutionState state) {
        return state.cacheClass.getNamedParameter(5);
    }
    
    @Benchmark
    public int directCache(ExecutionState state) {
        return state.directCacheClass.directCache(5);
    }
}
