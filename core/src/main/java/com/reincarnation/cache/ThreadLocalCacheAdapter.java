package com.reincarnation.cache;

import java.util.concurrent.Callable;

/**
 * <p>
 * Description: ThreadLocalCacheAdapter
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author twj
 * @version 1.0
 */
public interface ThreadLocalCacheAdapter {
    /**
     * Start caching in current thread
     */
    void start();
    
    /**
     * End caching in current thread
     */
    void end();
    
    /**
     * Returns the value associated with the {@code key} in this cache, obtaining that value from the
     * {@code callable} if necessary.
     */
    <T> T getOrElse(int key, Callable<T> callable) throws Exception;
}
