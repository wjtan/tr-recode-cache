package com.reincarnation.cache.benchmark.threadlocal;

import com.reincarnation.cache.annotation.ThreadLocalCached;

/**
 * <p>Description: CacheObject</p>
 * <p>Copyright: 2017</p>
 *
 * @author twj
 * @version 1.0
 */
class CacheObject {
    int value;
    
    int getBase() {
        return value;
    }
    
    @ThreadLocalCached
    int getCached() {
        return value;
    }
}