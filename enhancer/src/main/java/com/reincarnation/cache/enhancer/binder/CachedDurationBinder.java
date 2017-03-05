package com.reincarnation.cache.enhancer.binder;

import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.interceptor.annotation.CacheDuration;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationDescription.Loadable;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation.Target;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.Assigner.Typing;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;

/**
 * <p>
 * Description: CacheDurationBinder
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public enum CachedDurationBinder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<CacheDuration> {
    INSTANCE;
    
    @Override
    public Class<CacheDuration> getHandledType() {
        return CacheDuration.class;
    }
    
    @Override
    public ParameterBinding<?> bind(Loadable<CacheDuration> annotation, MethodDescription source, ParameterDescription target,
                                    Target implementationTarget, Assigner assigner,
                                    Typing typing) {
        TypeDescription parameterType = target.getType().asErasure();
        if (!parameterType.represents(int.class)) {
            throw new IllegalStateException("The " + target + " method's " + target.getIndex() +
                                            " parameter is annotated with a CacheDuration annotation with an argument not representing a int type");
        }
        
        AnnotationDescription sourceAnnotation = source.getDeclaredAnnotations().ofType(Cached.class);
        Cached cached = sourceAnnotation.prepare(Cached.class).loadSilent();
        int duration = cached.timeToLiveSeconds();
        if (duration > 0) {
            return new MethodDelegationBinder.ParameterBinding.Anonymous(IntegerConstant.forValue(duration));
        } else {
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }
    }
    
}
