/*
 * Adapted from graphql-spqr 0.12.4 by Bojan Tomic,
 * licensed under the Apache License, Version 2.0.
 * https://github.com/leangen/graphql-spqr
 */
package me.retrodaredevil.solarthing.rest.graphql.jackson;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import org.jspecify.annotations.NullUnmarked;
import tools.jackson.databind.JavaType;

@NullUnmarked
public final class TypeUtils {
	private TypeUtils() { throw new UnsupportedOperationException(); }

	static AnnotatedType toJavaType(JavaType jacksonType) {
		if (jacksonType.getRawClass().getTypeParameters().length > 0) {
			AnnotatedType[] paramTypes = jacksonType.getBindings().getTypeParameters().stream()
					.map(TypeUtils::toJavaType)
					.toArray(AnnotatedType[]::new);
			return TypeFactory.parameterizedAnnotatedClass(jacksonType.getRawClass(), jacksonType.getRawClass().getAnnotations(), paramTypes);
		}
		if (jacksonType.isArrayType()) {
			return TypeFactory.arrayOf(toJavaType(jacksonType.getContentType()), new Annotation[0]);
		}
		return GenericTypeReflector.annotate(jacksonType.getRawClass());
	}
}
