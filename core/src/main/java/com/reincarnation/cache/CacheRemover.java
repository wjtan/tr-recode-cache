package com.reincarnation.cache;

/**
 * <p>
 * Description: CacheRemover
 * </p>
 * <p>
 * Copyright: 2016
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public interface CacheRemover {
    public void remove(String prefix, Object... args);
}
