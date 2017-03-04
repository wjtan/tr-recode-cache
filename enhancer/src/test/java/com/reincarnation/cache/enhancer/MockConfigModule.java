package com.reincarnation.cache.enhancer;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;

import static com.reincarnation.cache.caffeine.CacheConfigKeys.CAFFEINE_SIZE;
import static com.reincarnation.cache.caffeine.CacheConfigKeys.CAFFEINE_STATISTICS;
import static org.mockito.Mockito.*;

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
