package com.github.mozvip.swagger.dropwizard;

import io.swagger.jersey.SwaggerJersey2Jaxrs;

import java.lang.reflect.Type;
import java.util.Set;

public class SwaggerDropwizardExtension extends SwaggerJersey2Jaxrs {

    @Override
    protected boolean shouldIgnoreClass(Class<?> cls) {
        return super.shouldIgnoreClass(cls);
    }

    @Override
    protected boolean shouldIgnoreType(Type type, Set<Type> typesToSkip) {
        return super.shouldIgnoreType(type, typesToSkip);
    }


}
