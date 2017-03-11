package com.reincarnation.cache.enhancer.binder;

import static net.bytebuddy.matcher.ElementMatchers.fieldType;

import com.reincarnation.cache.CacheAdapter;
import com.reincarnation.interceptor.annotation.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import net.bytebuddy.description.annotation.AnnotationDescription.Loadable;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation.Target;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.Assigner.Typing;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;

/**
 * <p>
 * Description: CacheBinder
 * </p>
 * <p>
 * Copyright: 2017
 * </p>
 * 
 * @author Denom
 */
public enum CacheBinder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<Cache> {
    INSTANCE;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheBinder.class);
    
    private static final TypeDescription CACHE_ADAPTER_TYPE = new TypeDescription.ForLoadedType(CacheAdapter.class);
    
    @Override
    public Class<Cache> getHandledType() {
        return Cache.class;
    }
    
    @Override
    public ParameterBinding<?> bind(Loadable<Cache> annotation, MethodDescription source, ParameterDescription target,
                                    Target implementationTarget, Assigner assigner, Typing typing) {
        
        TypeDescription parameterType = target.getType().asErasure();
        if (!parameterType.equals(CACHE_ADAPTER_TYPE)) {
            throw new IllegalStateException("The " + target + " method's " + target.getIndex() +
                                            " parameter is annotated with a Cache annotation with an argument not representing a CacheAdapter type");
        }
        
        Optional<FieldDescription> resolution = getField(implementationTarget.getInstrumentedType(), CACHE_ADAPTER_TYPE);
        if (!resolution.isPresent()) {
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }
        
        FieldDescription fieldDescription = resolution.get();
        StackManipulation stackManipulation = new StackManipulation.Compound(MethodVariableAccess.loadThis(),
                                                                             FieldAccess.forField(fieldDescription).read(),
                                                                             assigner.assign(fieldDescription.getType(), target.getType(),
                                                                                             RuntimeType.Verifier.check(target)));
        if (stackManipulation.isValid()) {
            return new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
        } else {
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }
    }
    
    // Resolve by fieldType
    Optional<FieldDescription> getField(TypeDescription typeDescription, TypeDescription fieldType) {
        for (TypeDefinition typeDefinition : typeDescription) {
            FieldList<?> candidates = typeDefinition.getDeclaredFields().filter(fieldType(fieldType));
            
            if (candidates.size() == 1) {
                return Optional.of(candidates.getOnly());
            } else if (candidates.isEmpty()) {
                LOGGER.trace("Cannot find " + fieldType + " in " + typeDescription);
            } else if (candidates.size() > 1) {
                LOGGER.trace("More than 1 " + fieldType + " in " + typeDescription);
            }
        }
        return Optional.empty();
    }
}
