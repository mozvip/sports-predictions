package com.github.mozvip.swagger.dropwizard;

import io.dropwizard.auth.Auth;
import io.swagger.jaxrs.ext.SwaggerExtension;
import io.swagger.jersey.SwaggerJersey2Jaxrs;
import io.swagger.models.parameters.Parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SwaggerDropwizardExtension extends SwaggerJersey2Jaxrs {

    @Override
    public List<Parameter> extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip, Iterator<SwaggerExtension> chain) {
        List<Parameter> parameters = new ArrayList<Parameter>();

        if (shouldIgnoreType(type, typesToSkip)) {
            return parameters;
        }
        for (final Annotation annotation : annotations) {
            // just handle the dropwizard specific annotation
            if (annotation instanceof Auth) {
                return parameters;
            }
        }

        // Only call down to the other items in the chain if no parameters were produced
        if (parameters.isEmpty()) {
            parameters = super.extractParameters(annotations, type, typesToSkip, chain);
        }

        return parameters;
    }

}
