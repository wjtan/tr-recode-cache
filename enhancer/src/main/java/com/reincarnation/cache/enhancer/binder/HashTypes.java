package com.reincarnation.cache.enhancer.binder;

import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.reincarnation.cache.util.HashFunction;

import java.util.HashMap;
import java.util.Map;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;

/**
 * <p>
 * Description: HashTypes
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 *
 * @author Denom
 * @version 1.0
 */
final class HashTypes {
    private static final String OBJECT_TYPE = "java.lang.Object";
    
    public static final TypeDescription HASH_FUNCTION_TYPE = new TypeDescription.ForLoadedType(HashFunction.class);
    
    public static final MethodDescription CONSTRUCTOR = HASH_FUNCTION_TYPE.getDeclaredMethods()
                                                                          .filter(isConstructor().and(takesArguments(1)))
                                                                          .getOnly();
    
    public static final MethodDescription GET_HASH_METHOD = HASH_FUNCTION_TYPE.getDeclaredMethods()
                                                                              .filter(named("getResult"))
                                                                              .getOnly();
    
    private static final Map<String, MethodDescription> HASH_METHODS = new HashMap<>();
    
    static {
        for (MethodDescription method : HASH_FUNCTION_TYPE.getDeclaredMethods()) {
            if ("hash".equals(method.getActualName())) {
                for (ParameterDescription parameter : method.getParameters()) {
                    HASH_METHODS.put(parameter.getType().getTypeName(), method);
                }
            }
        }
    }
    
    public static MethodDescription getHashMethod(TypeDescription.Generic sourceParameter) {
        String typeName = sourceParameter.getTypeName();
        if (HASH_METHODS.containsKey(typeName)) {
            return HASH_METHODS.get(typeName);
        } else {
            return HASH_METHODS.get(OBJECT_TYPE);
        }
    }
    
    private HashTypes() {
    }
}
