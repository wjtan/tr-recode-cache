package com.reincarnation.cache.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * <p>
 * Description: TimedObjecTest
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class TimedObjectTest {
    @Test
    public void timedObjectShouldExpire() throws InterruptedException {
        TimedObject<Integer> obj = new TimedObject<>(5, 1);
        
        assertThat(obj.isExpired()).isFalse();
        
        Thread.sleep(1001);
        
        assertThat(obj.isExpired()).isTrue();
    }
}
