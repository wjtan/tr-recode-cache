package com.reincarnation.cache.enhancer.binder;

import static com.reincarnation.cache.enhancer.binder.BinderUtils.hasCacheKeyAnnotation;
import static com.reincarnation.cache.enhancer.binder.BinderUtils.writeParameters;
import static com.reincarnation.cache.enhancer.binder.HashTypes.CONSTRUCTOR;
import static com.reincarnation.cache.enhancer.binder.HashTypes.GET_HASH_METHOD;
import static com.reincarnation.cache.enhancer.binder.HashTypes.HASH_FUNCTION_TYPE;

import com.reincarnation.cache.annotation.CacheRemove;
import com.reincarnation.cache.annotation.CacheRemoves;
import com.reincarnation.interceptor.annotation.GeneratedHash;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationDescription.Loadable;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
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
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;

/**
 * <p>
 * Description: CacheRemoveHashBinder
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public enum CacheRemoveHashBinder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<GeneratedHash> {
    INSTANCE;
    
    @Override
    public Class<GeneratedHash> getHandledType() {
        return GeneratedHash.class;
    }
    
    @Override
    public ParameterBinding<?> bind(Loadable<GeneratedHash> annotation, MethodDescription source, ParameterDescription target,
                                    Target implementationTarget, Assigner assigner, Typing typing) {
        
        TypeDescription parameterType = target.getType().asErasure();
        
        boolean hasCacheKey = hasCacheKeyAnnotation(source);
        
        StackManipulation stackManipulation;
        if (source.getDeclaredAnnotations().isAnnotationPresent(CacheRemove.class)) {
            if (!parameterType.represents(int.class)) {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            
            stackManipulation = writeHash(source, hasCacheKey);
        } else if (source.getDeclaredAnnotations().isAnnotationPresent(CacheRemoves.class)) {
            if (!parameterType.isArray() || !parameterType.getComponentType().represents(int.class)) {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            
            stackManipulation = writeHashes(source, assigner, typing, hasCacheKey);
        } else {
            stackManipulation = StackManipulation.Illegal.INSTANCE;
        }
        
        if (stackManipulation.isValid()) {
            return new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
        } else {
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }
    }
    
    private static StackManipulation writeHash(MethodDescription source, boolean hasCacheKey) {
        List<StackManipulation> stackManipulations = new ArrayList<>();
        
        // new HashFunction(PrimaryKey)
        stackManipulations.add(TypeCreation.of(HASH_FUNCTION_TYPE));
        stackManipulations.add(Duplication.of(HASH_FUNCTION_TYPE));
        stackManipulations.add(IntegerConstant.forValue(getPrimaryKey(source).hashCode()));
        stackManipulations.add(MethodInvocation.invoke(CONSTRUCTOR).special(HASH_FUNCTION_TYPE));
        
        writeParameters(source.getParameters().asDefined(), stackManipulations, hasCacheKey);
        
        // getHash()
        stackManipulations.add(MethodInvocation.invoke(GET_HASH_METHOD).virtual(HASH_FUNCTION_TYPE));
        
        return new StackManipulation.Compound(stackManipulations);
    }
    
    protected static StackManipulation writeHashes(MethodDescription source, Assigner assigner, Typing typing, boolean hasCacheKey) {
        List<StackManipulation> arrayStackManipulations = new ArrayList<>(source.getParameters().size());
        
        TypeDescription intType = new TypeDescription.ForLoadedType(int.class);
        ArrayFactory arrayFactory = ArrayFactory.forType(intType.asGenericType());
        
        AnnotationDescription methodAnnotation = source.getDeclaredAnnotations().ofType(CacheRemoves.class);
        CacheRemoves cacheRemoves = methodAnnotation.prepare(CacheRemoves.class).load();
        
        ParameterList<ParameterDescription.InDefinedShape> parameters = source.getParameters().asDefined();
        
        for (CacheRemove cacheRemove : cacheRemoves.value()) {
            String key = cacheRemove.value();
            if (Strings.isNullOrEmpty(key)) {
                throw new IllegalStateException("CacheRemove has not set value");
            }
            
            List<StackManipulation> stackManipulations = new ArrayList<>();
            
            // new HashFunction(PrimaryKey)
            stackManipulations.add(TypeCreation.of(HASH_FUNCTION_TYPE));
            stackManipulations.add(Duplication.of(HASH_FUNCTION_TYPE));
            stackManipulations.add(IntegerConstant.forValue(key.hashCode()));
            stackManipulations.add(MethodInvocation.invoke(CONSTRUCTOR).special(HASH_FUNCTION_TYPE));
            
            writeParameters(parameters, stackManipulations, hasCacheKey);
            
            // getHash()
            stackManipulations.add(MethodInvocation.invoke(GET_HASH_METHOD).virtual(HASH_FUNCTION_TYPE));
            
            // Assign to array
            stackManipulations.add(assigner.assign(intType.asGenericType(), arrayFactory.getComponentType(), typing));
            
            StackManipulation stackManipulation = new StackManipulation.Compound(stackManipulations);
            if (stackManipulation.isValid()) {
                arrayStackManipulations.add(stackManipulation);
            } else {
                throw new IllegalStateException("CacheRemove stack manipulation is invalid");
            }
        }
        
        return new MethodDelegationBinder.ParameterBinding.Anonymous(arrayFactory.withValues(arrayStackManipulations));
    }
    
    protected static String getPrimaryKey(MethodDescription source) {
        AnnotationDescription methodAnnotation = source.getDeclaredAnnotations().ofType(CacheRemove.class);
        CacheRemove cacheRemove = methodAnnotation.prepare(CacheRemove.class).load();
        String key = cacheRemove.value();
        if (Strings.isNullOrEmpty(key)) {
            throw new IllegalStateException("CacheRemove has not set value");
        }
        return key;
    }
}
