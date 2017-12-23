package com.reincarnation.cache.enhancer;

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;

/**
 * <p>
 * Description: CacheAgent
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public final class CacheAgent {
    private CacheAgent() {
    }
    
    public static void premain(String arguments,
                               Instrumentation instrumentation) {
        
        CachePlugin plugin = new CachePlugin();
        
        new AgentBuilder.Default().type(plugin)
                                  .transform((builder, type, classLoader, module) -> plugin.apply(builder, type))
                                  .installOn(instrumentation);
    }
}
