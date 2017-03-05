package com.reincarnation.cache.enhancer.annotation;

import com.reincarnation.cache.annotation.IgnoreCacheEnhancer;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

/**
 * <p>
 * Description: IgnoreCacheEnhancerImpl
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class IgnoreCacheEnhancerImpl implements IgnoreCacheEnhancer {
    @Override
    public Class<? extends Annotation> annotationType() {
        return IgnoreCacheEnhancer.class;
    }
}
