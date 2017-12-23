package com.reincarnation.cache.benchmark.threadlocal;

import com.reincarnation.cache.ThreadLocalCacheAdapter;
import com.reincarnation.cache.benchmark.MockConfigModule;
import com.reincarnation.cache.simple.SimpleCacheModule;

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
import org.openjdk.jmh.annotations.TearDown;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Description: ThreadLocalCacheBenchmark
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author twj
 * @version 1.0
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ThreadLocalCacheBenchmark {
    @State(Scope.Benchmark)
    public static class ExecutionState {
        private Injector injector;
        private ThreadLocalCacheAdapter cache;
        
        @Setup(Level.Trial)
        public void doSetup() {
            List<Module> modules = new ArrayList<>();
            modules.add(new MockConfigModule());
            modules.add(new SimpleCacheModule());
            modules.add(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CacheObject.class);
                }
            });
            
            injector = Guice.createInjector(modules);
            cache = injector.getInstance(ThreadLocalCacheAdapter.class);
        }
        
        @Setup(Level.Iteration)
        public void doInject() {
            object = injector.getInstance(CacheObject.class);
            
            cache.start();
        }
        
        @TearDown(Level.Iteration)
        public void shutdown() {
            cache.end();
        }
        
        CacheObject object;
    }
    
    @Benchmark
    public int baseline(ExecutionState state) {
        return state.object.getBase();
    }
    
    @Benchmark
    public int cached(ExecutionState state) {
        return state.object.getCached();
    }
}
