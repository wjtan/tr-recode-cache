package com.reincarnation.cache.enhancer.binder;

import static com.reincarnation.cache.enhancer.binder.BinderUtils.hasCacheKeyAnnotation;
import static com.reincarnation.cache.enhancer.binder.BinderUtils.writeParameters;
import static com.reincarnation.cache.enhancer.binder.HashTypes.CONSTRUCTOR;
import static com.reincarnation.cache.enhancer.binder.HashTypes.GET_HASH_METHOD;
import static com.reincarnation.cache.enhancer.binder.HashTypes.HASH_FUNCTION_TYPE;

import com.reincarnation.cache.annotation.Cached;
import com.reincarnation.inceptor.annotation.GeneratedHash;

import java.util.ArrayList;
import java.util.List;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationDescription.Loadable;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation.Target;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.Assigner.Typing;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;

/**
 * <p>
 * Description: CachedHashBinder
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public enum CachedHashBinder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<GeneratedHash> {
    INSTANCE;
    
    @Override
    public Class<GeneratedHash> getHandledType() {
        return GeneratedHash.class;
    }
    
    @Override
    public ParameterBinding<?> bind(Loadable<GeneratedHash> annotation, MethodDescription source, ParameterDescription target,
                                    Target implementationTarget,
                                    Assigner assigner, Typing typing) {
        
        TypeDescription parameterType = target.getType().asErasure();
        if (!parameterType.represents(int.class)) {
            
            throw new IllegalStateException("The " + target + " method's " + target.getIndex() +
                                            " parameter is annotated with a GeneratedHash annotation with an argument not representing a int type");
        }
        
        if (!source.getDeclaredAnnotations().isAnnotationPresent(Cached.class)) {
            throw new IllegalStateException(source + " method is not annotated with CacheWrite");
        }
        
        StackManipulation stackManipulation = writeSingleHash(source);
        
        if (stackManipulation.isValid()) {
            return new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
        } else {
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }
    }
    
    private static StackManipulation writeSingleHash(MethodDescription source) {
        List<StackManipulation> stackManipulations = new ArrayList<>();
        
        // new HashFunction(PrimaryKey)
        stackManipulations.add(TypeCreation.of(HASH_FUNCTION_TYPE));
        stackManipulations.add(Duplication.of(HASH_FUNCTION_TYPE));
        stackManipulations.add(IntegerConstant.forValue(getPrimaryKey(source).hashCode()));
        stackManipulations.add(MethodInvocation.invoke(CONSTRUCTOR).special(HASH_FUNCTION_TYPE));
        
        boolean hasCacheKey = hasCacheKeyAnnotation(source);
        
        writeParameters(source.getParameters().asDefined(), stackManipulations, hasCacheKey);
        
        // getHash()
        stackManipulations.add(MethodInvocation.invoke(GET_HASH_METHOD).virtual(HASH_FUNCTION_TYPE));
        
        return new StackManipulation.Compound(stackManipulations);
    }
    
    private static String getPrimaryKey(MethodDescription source) {
        AnnotationDescription annotation = source.getDeclaredAnnotations().ofType(Cached.class);
        Cached cached = annotation.prepare(Cached.class).loadSilent();
        String key = cached.value();
        
        if (key != null && !key.trim().isEmpty()) {
            return key;
        }
        return source.toString();
    }
}
