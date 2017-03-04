package com.reincarnation.cache.benchmark;

import static com.reincarnation.cache.caffeine.CacheConfigKeys.CAFFEINE_SIZE;
import static com.reincarnation.cache.caffeine.CacheConfigKeys.CAFFEINE_STATISTICS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;

/**
 * <p>
 * Description: MockConfigModule
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class MockConfigModule extends AbstractModule {
    
    @Override
    protected void configure() {
        Config config = mock(Config.class);
        when(config.getBoolean(CAFFEINE_STATISTICS)).thenReturn(false);
        when(config.getInt(CAFFEINE_SIZE)).thenReturn(1000);
        bind(Config.class).toInstance(config);
    }
    
}
