package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.generator.JavaDeprecationMappingConfig;
import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.TypedElement;
import io.leangen.graphql.metadata.exceptions.TypeMappingException;
import io.leangen.graphql.metadata.execution.FieldAccessor;
import io.leangen.graphql.metadata.execution.MethodInvoker;
import io.leangen.graphql.metadata.strategy.query.*;
import io.leangen.graphql.metadata.strategy.value.Property;
import io.leangen.graphql.util.ClassUtils;
import io.leangen.graphql.util.ReservedStrings;
import io.leangen.graphql.util.Utils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

public class JacksonResolverBuilder implements ResolverBuilder {

	private ObjectMapper objectMapper = new ObjectMapper();
	private String[] basePackages;
	private JavaDeprecationMappingConfig javaDeprecationConfig;

	public JacksonResolverBuilder() {
		this(new String[0]);
	}

	public JacksonResolverBuilder(String... basePackages) {
		withBasePackages(basePackages);
		withJavaDeprecation(new JavaDeprecationMappingConfig(true, "Deprecated"));
	}
	public JacksonResolverBuilder withObjectMapper(ObjectMapper objectMapper){
		this.objectMapper = objectMapper;
		return this;
	}

	public JacksonResolverBuilder withBasePackages(String... basePackages) {
		this.basePackages = basePackages;
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
//		Set<Property> properties = ClassUtils.getProperties(ClassUtils.getRawType(params.getBeanType().getType()));
//		Collection<Resolver> propertyAccessors = buildPropertyAccessors(properties.stream(), params);
//		Collection<Resolver> methodInvokers = buildMethodInvokers(params, (method, par) -> isQuery(method, par) && properties.stream().noneMatch(prop -> prop.getGetter().equals(method)), OperationDefinition.Operation.QUERY, true);
//		Collection<Resolver> fieldAccessors = buildFieldAccessors(params);
//		return Utils.concat(methodInvokers.stream(), propertyAccessors.stream(), fieldAccessors.stream()).collect(Collectors.toSet());
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
	private Collection<Resolver> buildResolvers(ResolverBuilderParams params){
		AnnotatedType beanType = params.getBeanType();
		Class<?> rawType = ClassUtils.getRawType(beanType.getType());
		if (rawType.isArray() || rawType.isPrimitive()) return Collections.emptyList();

		BeanDescription bean = objectMapper.getSerializationConfig().introspect(objectMapper.constructType(rawType));
		List<BeanPropertyDefinition> properties = bean.findProperties();
		List<Resolver> r = new ArrayList<>();
		for(BeanPropertyDefinition property : properties){
			String propertyName = property.getName();
			if(property.hasGetter()){
				Method method = property.getGetter().getMember();
				@GraphQLNonNull
				TypedElement element = new TypedElement(getReturnType(method, params), method);
				r.add(new Resolver(
						propertyName,
						"no description yet",
						null, // no deprecation
						false, // we won't mess with this
						new MethodInvoker(method, beanType),
						element,
						Collections.emptyList(),
						null // complexity stuff here // not implemented yet
				));
			} else if(property.hasField()){
				Field field = (Field) property.getField().getMember();
				TypedElement element = new TypedElement(getFieldType(field, params), field);
				r.add(new Resolver(
						propertyName,
						"no description yet",
						null, // no deprecation
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
