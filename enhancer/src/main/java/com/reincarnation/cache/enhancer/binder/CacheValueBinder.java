package com.reincarnation.cache.enhancer.binder;

import com.reincarnation.inceptor.annotation.CacheValue;

import net.bytebuddy.description.annotation.AnnotationDescription.Loadable;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation.Target;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.Assigner.Typing;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;

/**
 * <p>
 * Description: CacheValueBinder
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public enum CacheValueBinder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<CacheValue> {
    INSTANCE;
    
    @Override
    public Class<CacheValue> getHandledType() {
        return CacheValue.class;
    }
    
    @Override
    public ParameterBinding<?> bind(Loadable<CacheValue> annotation, MethodDescription source, ParameterDescription target, Target implementationTarget,
                                    Assigner assigner, Typing typing) {
        
        int lastSize = 0;
        int offset = 1;
        for (ParameterDescription parameter : source.getParameters().asDefined()) {
            TypeDescription.Generic sourceParameter = parameter.getType();
            
            // If has CacheValue
            if (parameter.getDeclaredAnnotations().isAnnotationPresent(com.reincarnation.cache.annotation.CacheValue.class)) {
                StackManipulation stackManipulation = new StackManipulation.Compound(MethodVariableAccess.of(sourceParameter).loadFrom(offset),
                                                                                     assigner.assign(sourceParameter, target.getType(),
                                                                                                     RuntimeType.Verifier.check(target)));
                return new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
            }
            
            lastSize = sourceParameter.getStackSize().getSize();
            offset += lastSize;
        }
        
        // If no parameters
        if (lastSize == 0) {
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }
        
        int lastParameter = source.getParameters().size() - 1;
        TypeDescription.Generic sourceParameter = source.getParameters().asTypeList().get(lastParameter);
        offset -= lastSize;
        
        StackManipulation stackManipulation = new StackManipulation.Compound(MethodVariableAccess.of(sourceParameter).loadFrom(offset),
                                                                             assigner.assign(sourceParameter, target.getType(),
                                                                                             RuntimeType.Verifier.check(target)));
        return new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
    }
}
