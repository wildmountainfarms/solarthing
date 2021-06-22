package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.generator.JavaDeprecationMappingConfig;
import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.TypedElement;
import io.leangen.graphql.metadata.exceptions.TypeMappingException;
import io.leangen.graphql.metadata.execution.FieldAccessor;
import io.leangen.graphql.metadata.execution.MethodInvoker;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilderParams;
import io.leangen.graphql.util.ClassUtils;
import me.retrodaredevil.solarthing.annotations.GraphQLExclude;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JacksonResolverBuilder implements ResolverBuilder {

	private ObjectMapper objectMapper = new ObjectMapper();
	private JavaDeprecationMappingConfig javaDeprecationConfig;

	public JacksonResolverBuilder() {
		withJavaDeprecation(new JavaDeprecationMappingConfig(true, "Deprecated"));
	}
	public JacksonResolverBuilder withObjectMapper(ObjectMapper objectMapper){
		this.objectMapper = objectMapper;
		return this;
	}

	/**
	 * Sets whether the {@code Deprecated} annotation should map to GraphQL deprecation
	 *
	 * @param javaDeprecation Whether the {@code Deprecated} maps to GraphQL deprecation
	 * @return This builder instance to allow chained calls
	 */
	public JacksonResolverBuilder withJavaDeprecationRespected(boolean javaDeprecation) {
		this.javaDeprecationConfig = new JavaDeprecationMappingConfig(javaDeprecation, "Deprecated");
		return this;
	}

	/**
	 * Sets whether and how the {@code Deprecated} annotation should map to GraphQL deprecation
	 *
	 * @param javaDeprecationConfig Configures if and how {@code Deprecated} maps to GraphQL deprecation
	 * @return This builder instance to allow chained calls
	 */
	public JacksonResolverBuilder withJavaDeprecation(JavaDeprecationMappingConfig javaDeprecationConfig) {
		this.javaDeprecationConfig = javaDeprecationConfig;
		return this;
	}

	@Override
	public Collection<Resolver> buildQueryResolvers(ResolverBuilderParams params) {
		return buildResolvers(params);
	}

	@Override
	public Collection<Resolver> buildMutationResolvers(ResolverBuilderParams params) {
		return Collections.emptyList();
	}

	@Override
	public Collection<Resolver> buildSubscriptionResolvers(ResolverBuilderParams params) {
		return Collections.emptyList();
	}
	private String getDeprecationReason(AnnotatedMember member) {
		if (!javaDeprecationConfig.enabled || !member.hasAnnotation(Deprecated.class)) {
			return null;
		}
		return javaDeprecationConfig.deprecationReason;
	}
	private Collection<Resolver> buildResolvers(ResolverBuilderParams params){
		AnnotatedType beanType = params.getBeanType();
		Class<?> rawType = ClassUtils.getRawType(beanType.getType());
		if (rawType.isArray() || rawType.isPrimitive()) return Collections.emptyList();

		BeanDescription bean = objectMapper.getSerializationConfig().introspect(objectMapper.constructType(rawType));
		AnnotatedMember jsonValueAccessor = bean.findJsonValueAccessor();
		if(jsonValueAccessor != null){
			System.err.println("@JsonValue is present! Bad!");
		}

		List<BeanPropertyDefinition> properties = bean.findProperties();
		List<Resolver> r = new ArrayList<>();
		for(BeanPropertyDefinition property : properties){
			AnnotatedMember accessor = property.getAccessor();
			if (accessor == null) {
				// accessor may be null if JsonIgnore is present
				continue;
			}

			if(AnnotationUtil.hasAnnotation(GraphQLIgnore.class, accessor)
					|| AnnotationUtil.hasAnnotation(GraphQLInclude.class, accessor)
					|| AnnotationUtil.hasAnnotation(GraphQLExclude.class, accessor)) {
				continue;
			}
			String propertyName = property.getName();
			JsonPropertyDescription descriptionAnnotation = AnnotationUtil.getAnnotation(JsonPropertyDescription.class, accessor);
			String description = descriptionAnnotation == null ? null : descriptionAnnotation.value();
			if(property.hasGetter()){
				Method method = property.getGetter().getMember();
				TypedElement element = new TypedElement(getReturnType(method, params), method);
				r.add(new Resolver(
						propertyName,
						description,
						getDeprecationReason(accessor),
						false, // we won't mess with this
						new MethodInvoker(method, beanType),
						element,
						Collections.emptyList(), // no arguments
						null // complexity stuff here // not implemented yet // I don't know what this is
				));
			} else if(property.hasField()){
				Field field = (Field) property.getField().getMember();
				TypedElement element = new TypedElement(getFieldType(field, params), field);
				r.add(new Resolver(
						propertyName,
						description,
						getDeprecationReason(accessor),
						false, // we won't mess with this
						new FieldAccessor(field, beanType),
						element,
						Collections.emptyList(),
						null // complexity stuff here // not implemented yet
				));
			} else {
				System.err.println("property=" + property + " is unsupported! name=" + property.getName() + " accessor=" + property.getAccessor());
			}
		}
		return r;
	}


	// region copied stuff
	protected AnnotatedType getFieldType(Field field, ResolverBuilderParams params) {
		try {
			return params.getTypeTransformer().transform(ClassUtils.getFieldType(field, params.getBeanType()));
		} catch (TypeMappingException e) {
			throw TypeMappingException.ambiguousMemberType(field, params.getBeanType(), e);
		}
	}

	protected AnnotatedType getReturnType(Method method, ResolverBuilderParams params) {
		try {
			return params.getTypeTransformer().transform(ClassUtils.getReturnType(method, params.getBeanType()));
		} catch (TypeMappingException e) {
			throw TypeMappingException.ambiguousMemberType(method, params.getBeanType(), e);
		}
	}
	// endregion
}
