package com.reincarnation.cache.enhancer.binder;

import static com.reincarnation.cache.enhancer.binder.HashTypes.HASH_FUNCTION_TYPE;
import static com.reincarnation.cache.enhancer.binder.HashTypes.getHashMethod;

import com.reincarnation.cache.annotation.CacheKey;
import com.reincarnation.cache.annotation.CacheValue;

import java.util.List;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;

/**
 * <p>
 * Description: GeneratedHashBindUtils
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
public class BinderUtils {
    public static boolean hasCacheKeyAnnotation(MethodDescription source) {
        for (ParameterDescription parameter : source.getParameters().asDefined()) {
            if (parameter.getDeclaredAnnotations().isAnnotationPresent(CacheKey.class)) {
                return true;
            }
        }
        return false;
    }
    
    public static void writeParameters(ParameterList<ParameterDescription.InDefinedShape> parameters,
                                       List<StackManipulation> stackManipulations, boolean hasCacheKey) {
        if (hasCacheKey) {
            int offset = 1;
            for (ParameterDescription parameter : parameters) {
                TypeDescription.Generic sourceParameter = parameter.getType();
                
                // If hasCacheKey, only get hash for those parameter
                // Ignore CacheValue
                if (parameter.getDeclaredAnnotations().isAnnotationPresent(CacheKey.class)
                    && !parameter.getDeclaredAnnotations().isAnnotationPresent(CacheValue.class)) {
                    stackManipulations.add(MethodVariableAccess.of(sourceParameter).loadFrom(offset));
                    stackManipulations.add(MethodInvocation.invoke(getHashMethod(sourceParameter)).virtual(HASH_FUNCTION_TYPE));
                }
                offset += sourceParameter.getStackSize().getSize();
            }
        } else {
            int offset = 1;
            for (ParameterDescription parameter : parameters) {
                TypeDescription.Generic sourceParameter = parameter.getType();
                
                // Get hash for all parameters
                // Ignore CacheValue
                if (!parameter.getDeclaredAnnotations().isAnnotationPresent(CacheValue.class)) {
                    stackManipulations.add(MethodVariableAccess.of(sourceParameter).loadFrom(offset));
                    stackManipulations.add(MethodInvocation.invoke(getHashMethod(sourceParameter)).virtual(HASH_FUNCTION_TYPE));
                }
                offset += sourceParameter.getStackSize().getSize();
            }
        }
    }
}
