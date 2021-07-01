package me.retrodaredevil.solarthing.rest.graphql;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLSubscription;
import io.leangen.graphql.metadata.TypedElement;
import io.leangen.graphql.metadata.exceptions.TypeMappingException;
import io.leangen.graphql.metadata.strategy.query.*;
import io.leangen.graphql.metadata.strategy.value.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * An almost direct copy of {@link AnnotatedResolverBuilder}, but altered to use {@link AnnotationUtil} instead of directly checking
 * if something has an annotation.
 * <p>
 * This allows annotations declared in interfaces to be "inherited".
 */
public class SolarThingAnnotatedResolverBuilder extends PublicResolverBuilder {


	public SolarThingAnnotatedResolverBuilder() {
		this.operationInfoGenerator = new DefaultOperationInfoGenerator();
		this.argumentBuilder = new AnnotatedArgumentBuilder();
		this.propertyElementReducer = SolarThingAnnotatedResolverBuilder::annotatedElementReducer;
		withDefaultFilters();
	}

	@Override
	protected boolean isQuery(Method method, ResolverBuilderParams params) {
		return AnnotationUtil.hasAnnotation(GraphQLQuery.class, method);
	}

	@Override
	protected boolean isQuery(Field field, ResolverBuilderParams params) {
		return AnnotationUtil.hasAnnotation(GraphQLQuery.class, field);
	}

	@Override
	protected boolean isQuery(Property property, ResolverBuilderParams params) {
		return isQuery(property.getGetter(), params) || isQuery(property.getField(), params);
	}

	@Override
	protected boolean isMutation(Method method, ResolverBuilderParams params) {
		return AnnotationUtil.hasAnnotation(GraphQLMutation.class, method);
	}

	@Override
	protected boolean isSubscription(Method method, ResolverBuilderParams params) {
		return AnnotationUtil.hasAnnotation(GraphQLSubscription.class, method);
	}

	private static TypedElement annotatedElementReducer(TypedElement field, TypedElement getter) {
		if (field.isAnnotationPresent(GraphQLQuery.class) && getter.isAnnotationPresent(GraphQLQuery.class)) {
			throw new TypeMappingException("Ambiguous mapping of " + field);
		}
		return field.isAnnotationPresent(GraphQLQuery.class) ? field : getter;
	}
}
