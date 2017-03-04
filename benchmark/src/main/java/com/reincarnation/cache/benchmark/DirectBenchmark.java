package com.reincarnation.cache.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Description: Benchmark
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class DirectBenchmark {
    
    @State(Scope.Thread)
    public static class ExecutionState {
        public TestClass test = new TestClass();
    }
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int baseline(ExecutionState state) {
        return state.test.getValue(5);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int hashmap(ExecutionState state) {
        return state.test.getCached(5);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int caffeine(ExecutionState state) {
        return state.test.getCaffeine(5);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int hashmapCallable(ExecutionState state) {
        return state.test.getCachedCallable(5);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int caffeineCallable(ExecutionState state) {
        return state.test.getCaffeineCallable(5);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int caffeineIntercept(ExecutionState state) {
        return state.test.getCaffeineIntercept(5);
    }
}
