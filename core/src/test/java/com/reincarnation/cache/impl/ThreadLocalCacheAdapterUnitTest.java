package com.reincarnation.cache.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.reincarnation.cache.ThreadLocalCacheAdapter;
import com.reincarnation.cache.impl.ThreadLocalCacheAdapterImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Description: ThreadLocalCacheAdapterUnitTest
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author twj
 * @version 1.0
 */
public class ThreadLocalCacheAdapterUnitTest {
    private ThreadLocalCacheAdapter cache;
    
    @Before
    public void setup() {
        cache = new ThreadLocalCacheAdapterImpl();
    }
    
    @Test
    public void getShouldNotWork() throws Exception {
        int result = cache.getOrElse(1, () -> 1);
        assertThat(result).isEqualTo(1);
        
        int result2 = cache.getOrElse(1, () -> 2);
        assertThat(result2).isEqualTo(2);
    }
    
    @Test
    public void getShouldWork() throws Exception {
        cache.start();
        
        int result = cache.getOrElse(1, () -> 1);
        assertThat(result).isEqualTo(1);
        
        int result2 = cache.getOrElse(1, () -> 2);
        assertThat(result2).isEqualTo(1);
        
        cache.end();
    }
}
