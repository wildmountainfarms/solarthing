package me.retrodaredevil.solarthing.graphql.extras;

import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.TypedElement;
import io.leangen.graphql.metadata.exceptions.TypeMappingException;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilderParams;
import io.leangen.graphql.util.ClassUtils;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.Collections;

public class RoverStatusPacketExtraResolverBuilder implements ResolverBuilder {
	public RoverStatusPacketExtraResolverBuilder() {
	}
	@Override
	public Collection<Resolver> buildQueryResolvers(ResolverBuilderParams params) {
		Class<?> rawType = ClassUtils.getRawType(params.getBeanType().getType());
		if(!RoverStatusPacket.class.isAssignableFrom(rawType)) {
			return Collections.emptyList();
		}
		final Method method;
		try {
			method = getClass().getMethod("getControllerTemperatureFahrenheit", RoverStatusPacket.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		AnnotatedType thisClass = new SimpleAnnotatedType(getClass());
		TypedElement returnType = new TypedElement(getReturnType(method, params, thisClass), method);
		return Collections.singleton(new Resolver(
				"controllerTemperatureFahrenheit",
				"Controller temperature in fahrenheit",
				null,
				false,
				new StaticMethodExecutable(method, thisClass),
				returnType,
				Collections.emptyList(),
				null
		));
	}

	@Override
	public Collection<Resolver> buildMutationResolvers(ResolverBuilderParams params) {
		return Collections.emptyList();
	}

	@Override
	public Collection<Resolver> buildSubscriptionResolvers(ResolverBuilderParams params) {
		return Collections.emptyList();
	}
	public static float getControllerTemperatureFahrenheit(RoverStatusPacket roverStatusPacket) {
		return roverStatusPacket.getControllerTemperatureCelsius() * 9 / 5.0f + 32;
	}
	protected AnnotatedType getReturnType(Method method, ResolverBuilderParams params, AnnotatedType annotatedType) {
		try {
			return params.getTypeTransformer().transform(ClassUtils.getReturnType(method, annotatedType));
		} catch (TypeMappingException e) {
			throw TypeMappingException.ambiguousMemberType(method, annotatedType, e);
		}
	}

	private static class SimpleAnnotatedType implements AnnotatedType {
		private final Class<?> clazz;

		private SimpleAnnotatedType(Class<?> clazz) {
			this.clazz = clazz;
		}

		@Override
		public Type getType() {
			return clazz;
		}

		@Override
		public <T extends Annotation> T getAnnotation(@NotNull Class<T> aClass) {
			return clazz.getAnnotation(aClass);
		}

		@Override
		public Annotation[] getAnnotations() {
			return clazz.getAnnotations();
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return clazz.getDeclaredAnnotations();
		}
	}
}
