package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.leangen.graphql.metadata.strategy.query.OperationInfoGeneratorParams;
import io.leangen.graphql.metadata.strategy.query.PropertyOperationInfoGenerator;
import io.leangen.graphql.metadata.strategy.query.PublicResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilderParams;
import io.leangen.graphql.metadata.strategy.value.Property;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class SolarThingResolverBuilder extends PublicResolverBuilder {

	public SolarThingResolverBuilder(){
		withOperationInfoGenerator(new PropertyOperationInfoGenerator() {
			@Override
			public String name(OperationInfoGeneratorParams params) {
				GraphQLInclude graphQLInclude = AnnotationUtil.getAnnotation(GraphQLInclude.class, params.getElement());
				if(graphQLInclude == null){
					throw new NullPointerException("graphQLInclude is null! params: " + params.getElement().getElement());
				}
				return graphQLInclude.value();
			}

			@Override
			public String description(OperationInfoGeneratorParams params) {
				JsonPropertyDescription jsonPropertyDescription = AnnotationUtil.getAnnotation(JsonPropertyDescription.class, params.getElement());
				if(jsonPropertyDescription != null){
					return jsonPropertyDescription.value();
				}
				return super.description(params);
			}
		});
	}

	@Override
	protected boolean isQuery(Method method, ResolverBuilderParams params) {
		return AnnotationUtil.hasAnnotation(GraphQLInclude.class, method);
	}

	@Override
	protected boolean isQuery(Field field, ResolverBuilderParams params) {
		return AnnotationUtil.hasAnnotation(GraphQLInclude.class, field);
	}

	@Override
	protected boolean isQuery(Property property, ResolverBuilderParams params) {
		return isQuery(property.getGetter(), params) || isQuery(property.getField(), params);
	}

	@Override
	protected boolean isMutation(Method method, ResolverBuilderParams params) {
		return false;
	}

	@Override
	protected boolean isSubscription(Method method, ResolverBuilderParams params) {
		return false;
	}

//	private static TypedElement annotatedElementReducer(TypedElement field, TypedElement getter) {
//		if (field.isAnnotationPresent(GraphQLQuery.class) && getter.isAnnotationPresent(GraphQLQuery.class)) {
//			throw new TypeMappingException("Ambiguous mapping of " + field);
//		}
//		return field.isAnnotationPresent(GraphQLQuery.class) ? field : getter;
//	}

}
