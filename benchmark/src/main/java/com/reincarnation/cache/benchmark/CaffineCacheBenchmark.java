package com.reincarnation.cache.benchmark;

import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.cache.caffeine.CaffeineCacheModule;

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
 * Static Cache: 4214ms
 * Static Cache with key: 1588ms
 * Temporal Cache: 4729ms
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class CaffineCacheBenchmark {
    private static final String CACHEKEY = "KeyForCache";
    private static final String CACHEKEY2 = "KeyForCache2";
    private static final int VALUE1 = 2132;
    
    public static class CacheClass {
        @Cached
        public int get() {
            return VALUE1;
        }
        
        @Cached(value = CACHEKEY)
        public int getKey() {
            return VALUE1;
        }
        
        @Cached(timeToLiveSeconds = 1)
        public int getTimed() {
            return VALUE1;
        }
        
        @Cached(value = CACHEKEY2, timeToLiveSeconds = 1)
        public int getKeyTimed() {
            return VALUE1;
        }
    }
    
    @State(Scope.Thread)
    public static class ExecutionState {
        private Injector injector;
        
        @Setup(Level.Iteration)
        public void doSetup() {
            // Map<String, Object> configs = new HashMap<>();
            // configs.put(STATISTICS, false);
            // configs.put(CAFFEINE_STATIC_SIZE, 10);
            // configs.put(CAFFEINE_TEMPORAL_SIZE, 10);
            
            List<Module> modules = new ArrayList<>();
            modules.add(new CaffeineCacheModule());
            modules.add(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CacheClass.class);
                }
            });
            
            injector = Guice.createInjector(modules);
            cacheClass = injector.getInstance(CacheClass.class);
        }
        
        public CacheClass cacheClass;
    }
    
    // Static Cache
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int cache(ExecutionState state) {
        return state.cacheClass.get();
    }
    
    // Static Cache with key
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int cacheWithKey(ExecutionState state) {
        return state.cacheClass.getKey();
    }
    
    // Temporal Cache
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int temporalCache(ExecutionState state) {
        return state.cacheClass.getTimed();
    }
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int temporalCacheWithKey(ExecutionState state) {
        return state.cacheClass.getKeyTimed();
    }
}
