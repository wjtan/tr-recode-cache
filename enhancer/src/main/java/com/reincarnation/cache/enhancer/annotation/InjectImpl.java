package com.reincarnation.cache.enhancer.annotation;

import java.lang.annotation.Annotation;
import javax.inject.Inject;

/**
 * <p>
 * Description: InjectImpl
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public class InjectImpl implements Inject {
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return Inject.class;
    }
    
}
