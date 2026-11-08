/*
 * Adapted from graphql-spqr 0.12.4 by Bojan Tomic,
 * licensed under the Apache License, Version 2.0.
 * https://github.com/leangen/graphql-spqr
 */
package me.retrodaredevil.solarthing.rest.graphql.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.graphql.annotations.GraphQLInputField;
import io.leangen.graphql.execution.GlobalEnvironment;
import io.leangen.graphql.metadata.DefaultValue;
import io.leangen.graphql.metadata.InputField;
import io.leangen.graphql.metadata.TypeDiscriminatorField;
import io.leangen.graphql.metadata.TypedElement;
import io.leangen.graphql.metadata.exceptions.TypeMappingException;
import io.leangen.graphql.metadata.strategy.InclusionStrategy;
import io.leangen.graphql.metadata.strategy.InputFieldInclusionParams;
import io.leangen.graphql.metadata.strategy.type.TypeTransformer;
import io.leangen.graphql.metadata.strategy.value.InputFieldBuilder;
import io.leangen.graphql.metadata.strategy.value.InputFieldBuilderParams;
import io.leangen.graphql.metadata.strategy.value.InputFieldInfoGenerator;
import io.leangen.graphql.metadata.strategy.value.InputParsingException;
import io.leangen.graphql.metadata.strategy.value.ValueMapper;
import io.leangen.graphql.util.ClassUtils;
import io.leangen.graphql.util.Scalars;
import io.leangen.graphql.util.Utils;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.introspect.*;
import tools.jackson.databind.jsontype.NamedType;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.leangen.geantyref.GenericTypeReflector.isBoxType;
import static io.leangen.graphql.util.ClassUtils.isPrimitive;

@NullUnmarked
public class JacksonValueMapper implements ValueMapper, InputFieldBuilder {

	private final ObjectMapper objectMapper;
	private final InputFieldInfoGenerator inputInfoGen;

	private boolean mapDeserializableType = false;

	private static final Logger log = LoggerFactory.getLogger(JacksonValueMapper.class);

	public JacksonValueMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.inputInfoGen = new JacksonDefaultValueProvider.InputFieldInfo(objectMapper);
	}

	@Override
	public <T> T fromInput(Object graphQLInput, Type sourceType, AnnotatedType outputType) {
		try {
			return objectMapper.convertValue(graphQLInput, objectMapper.getTypeFactory().constructType(outputType.getType()));
		} catch (JacksonException e) {
			throw new InputParsingException(graphQLInput, outputType.getType(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T fromString(String json, AnnotatedType type) {
		if (json == null || String.class.equals(type.getType())) {
			return (T) json;
		}
		try {
			if (Scalars.isScalar(type.getType()) && !isPrimitive(type) && !isBoxType(type.getType())) {
				return (T) Scalars.toGraphQLScalarType(type.getType()).getCoercing().parseValue(json);
			}
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructType(type.getType()));
		} catch (JacksonException e) {
			throw new InputParsingException(json, type.getType(), e);
		}
	}

	@Override
	public String toString(Object output, AnnotatedType type) {
		if (output != null && Scalars.isScalar(type.getType())) {
			output = Scalars.toGraphQLScalarType(type.getType()).getCoercing().serialize(output);
		}
		if (output == null || output instanceof String) {
			return (String) output;
		}
		return objectMapper.writeValueAsString(output);
	}

	@Override
	public Set<InputField> getInputFields(InputFieldBuilderParams params) {
		JavaType javaType = objectMapper.getTypeFactory().constructType(params.getType().getType());
		BeanDescription originalDesc = objectMapper._deserializationContext().introspectBeanDescription(javaType);
		BeanDescription desc = originalDesc;
		JavaType refined = objectMapper.getTypeFactory().constructType(resolveDeserializableType(desc.getClassInfo(), params.getType(), javaType, objectMapper).getType());
		if (mapDeserializableType) {
			desc = objectMapper._deserializationContext().introspectBeanDescription(refined);
		}
		BeanDescription delegateDesc = findDelegate(desc);
		AnnotatedType type = delegateDesc == originalDesc ? params.getType() : TypeUtils.toJavaType(delegateDesc.getType());
		return delegateDesc.findProperties().stream()
				.filter(prop -> isIncluded(type, prop, isPropertyDeserializableInSubType(prop, TypeUtils.toJavaType(refined), params.getConcreteSubTypes()), params.getEnvironment().inclusionStrategy))
				.map(prop -> toInputField(type, prop, params.getEnvironment()))
				.collect(Collectors.toSet());
	}

	private InputField toInputField(AnnotatedType type, BeanPropertyDefinition prop, GlobalEnvironment environment) {
		ElementFactory elementFactory = new ElementFactory(type, environment.typeTransformer);
		return toInputField(elementFactory.fromProperty(prop), prop, objectMapper, environment);
	}

	private InputField toInputField(TypedElement element, BeanPropertyDefinition prop, ObjectMapper objectMapper, GlobalEnvironment environment) {
		AnnotatedType deserializableType = resolveDeserializableType(prop.getPrimaryMember(), element.getJavaType(), prop.getPrimaryType(), objectMapper);
		DefaultValue defaultValue = inputInfoGen.defaultValue(element.getElements(), element.getJavaType(), environment);
		return new InputField(prop.getName(), prop.getMetadata().getDescription(), element, deserializableType, defaultValue);
	}

	@Override
	public @Nullable TypeDiscriminatorField getTypeDiscriminatorField(InputFieldBuilderParams params) {
		JavaType javaType = objectMapper.constructType(params.getType().getType());
		DeserializationConfig deserializationConfig = objectMapper.deserializationConfig();
		AnnotatedClass clazz = AnnotatedClassResolver.resolve(deserializationConfig, javaType, deserializationConfig);
		AnnotationIntrospector annotationIntrospector = deserializationConfig.getAnnotationIntrospector();

		JsonTypeInfo.Value typeResolver = annotationIntrospector.findPolymorphicTypeInfo(deserializationConfig, clazz);
		String discriminatorFieldName = typeResolver != null
				? typeResolver.getPropertyName()
				: ValueMapper.TYPE_METADATA_FIELD_NAME;

		String[] explicitSubTypes = Utils.stream(deserializationConfig.getSubtypeResolver().collectAndResolveSubtypesByClass(deserializationConfig, clazz))
				.map(NamedType::getName)
				.filter(Utils::isNotEmpty)
				.toArray(String[]::new);
		//Explicit subtypes are always mapped
		if (Utils.isNotEmpty(explicitSubTypes)) {
			return new TypeDiscriminatorField(discriminatorFieldName, "Input type discriminator", explicitSubTypes);
		}
		String[] discoveredSubTypes = params.getConcreteSubTypes().stream()
				.map(GenericTypeReflector::annotate)
				.map(impl -> params.getEnvironment().typeInfoGenerator.generateTypeName(impl, params.getEnvironment().messageBundle))
				.toArray(String[]::new);
		//Discovered subtypes are only mapped if ambiguous
		if (discoveredSubTypes.length > 1) {
			return new TypeDiscriminatorField(discriminatorFieldName, "Input type discriminator", discoveredSubTypes);
		}
		return null;
	}

	private AnnotatedType resolveDeserializableType(Annotated accessor, AnnotatedType realType, JavaType baseType, ObjectMapper objectMapper) {
		AnnotationIntrospector introspector = objectMapper.deserializationConfig().getAnnotationIntrospector();
		try {
			JavaType mapped = objectMapper.deserializationConfig().mapAbstractType(baseType);
			JavaType refined = introspector.refineDeserializationType(objectMapper.deserializationConfig(), accessor, mapped);
			Class<?> raw = ClassUtils.getRawType(realType.getType());
			if (!refined.getRawClass().equals(raw)) {
				if (ClassUtils.isSuperClass(realType, refined.getRawClass())) {
					AnnotatedType candidate = GenericTypeReflector.getExactSubType(realType, refined.getRawClass());
					if (!ClassUtils.isMissingTypeParameters(candidate.getType())) {
						return candidate;
					}
				}
				return GenericTypeReflector.updateAnnotations(TypeUtils.toJavaType(refined), realType.getAnnotations());
			}
		} catch (DatabindException e) {
			/*no-op*/
		} catch (Exception e) {
			log.warn("Failed to determine the deserializable type for " + GenericTypeReflector.getTypeName(realType.getType())
					+ " due to an exception", e);
		}
		return realType;
	}

	private boolean isIncluded(AnnotatedType declaringType, BeanPropertyDefinition prop, boolean deserializableInSubType, InclusionStrategy inclusionStrategy) {
		List<AnnotatedElement> elements = Utils.flatten(
				Optional.ofNullable(prop.getConstructorParameter()).map(ElementFactory::getParameter),
				Optional.ofNullable(prop.getSetter()).map(Annotated::getAnnotated),
				Optional.ofNullable(prop.getGetter()).map(Annotated::getAnnotated),
				Optional.ofNullable(prop.getField()).map(Annotated::getAnnotated))
				.collect(Collectors.toList());

		InputFieldInclusionParams params = InputFieldInclusionParams.builder()
				.withType(declaringType)
				.withElementDeclaringClass(prop.getPrimaryMember().getDeclaringClass())
				.withElements(elements)
				.withDeserializationInfo(prop.couldDeserialize(), deserializableInSubType)
				.build();

		return inclusionStrategy.includeInputField(params);
	}

	private BeanDescription findDelegate(BeanDescription beanDesc) {
		AnnotationIntrospector introspector = objectMapper.deserializationConfig().getAnnotationIntrospector();
		for (AnnotatedMethod ctor : beanDesc.getFactoryMethods()) {
			JsonCreator.Mode creatorMode = introspector.findCreatorAnnotation(objectMapper.deserializationConfig(), ctor);
			if (creatorMode == JsonCreator.Mode.DELEGATING) {
				return objectMapper._deserializationContext().introspectBeanDescription(ctor.getParameterType(0));
			}
		}

		for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
			JsonCreator.Mode creatorMode = introspector.findCreatorAnnotation(objectMapper.deserializationConfig(), ctor);
			if (creatorMode == JsonCreator.Mode.DELEGATING) {
				return objectMapper._deserializationContext().introspectBeanDescription(ctor.getParameterType(0));
			}
		}
		return beanDesc;
	}

	private boolean isPropertyDeserializableInSubType(BeanPropertyDefinition prop, AnnotatedType deserializableType, List<Class<?>> concreteSubTypes) {
		return isPropertyDeserializable(prop, ClassUtils.getRawType(deserializableType.getType()))
				|| concreteSubTypes.stream().anyMatch(impl -> isPropertyDeserializable(prop, impl));
	}

	private boolean isPropertyDeserializable(BeanPropertyDefinition prop, Class<?> type) {
		JavaType javaType = objectMapper.getTypeFactory().constructType(type);
		BeanDescription desc = objectMapper._deserializationContext().introspectBeanDescription(javaType);
		return desc.findProperties().stream()
				.anyMatch(p -> p.getName().equals(prop.getName()) && p.couldDeserialize());
	}

	public JacksonValueMapper withDeserializableTypeAsPrimary() {
		this.mapDeserializableType = true;
		return this;
	}

	@Override
	public boolean supports(AnnotatedType type) {
		return true;
	}

	private static class ElementFactory {

		private final AnnotatedType type;
		private final TypeTransformer transformer;

		ElementFactory(AnnotatedType type, TypeTransformer typeTransformer) {
			this.type = type;
			this.transformer = typeTransformer;
		}

		TypedElement fromConstructorParameter(AnnotatedParameter ctorParam) {
			Executable constructor = (Executable) ctorParam.getOwner().getMember();
			Parameter parameter = constructor.getParameters()[ctorParam.getIndex()];
			AnnotatedType fieldType = transform(ClassUtils.getParameterTypes(constructor, type)[ctorParam.getIndex()], parameter);
			return new TypedElement(fieldType, parameter);
		}

		TypedElement fromSetter(AnnotatedMethod setterMethod) {
			Method setter = setterMethod.getAnnotated();
			AnnotatedType fieldType = transform(ClassUtils.getParameterTypes(setter, type)[0], setter, type);
			return new TypedElement(fieldType, setter);
		}

		TypedElement fromGetter(AnnotatedMethod getterMethod) {
			Method getter = getterMethod.getAnnotated();
			AnnotatedType fieldType = transform(ClassUtils.getReturnType(getter, type), getter, type);
			return new TypedElement(fieldType, getter);
		}

		TypedElement fromField(AnnotatedField fld) {
			Field field = fld.getAnnotated();
			AnnotatedType fieldType = transform(ClassUtils.getFieldType(field, type), field, type);
			return new TypedElement(fieldType, field);
		}

		TypedElement fromProperty(BeanPropertyDefinition prop) {
			Optional<TypedElement> constParam = Optional.ofNullable(prop.getConstructorParameter()).map(this::fromConstructorParameter);
			Optional<TypedElement> setter = Optional.ofNullable(prop.getSetter()).map(this::fromSetter);
			Optional<TypedElement> getter = Optional.ofNullable(prop.getGetter()).map(this::fromGetter);
			Optional<TypedElement> field = Optional.ofNullable(prop.getField()).map(this::fromField);

			Optional<TypedElement> mutator = Utils.flatten(constParam, setter, field).findFirst();
			Optional<TypedElement> explicit = Utils.flatten(constParam, setter, getter, field).filter(e -> e.isAnnotationPresent(GraphQLInputField.class)).findFirst();

			List<TypedElement> elements = Utils.flatten(explicit, mutator, field).collect(Collectors.toList());
			if (elements.isEmpty()) {
				elements = getter.map(Collections::singletonList).orElse(Collections.emptyList());
			}
			return new TypedElement(elements);
		}

		AnnotatedType transform(AnnotatedType type, Member member, AnnotatedType declaringType) {
			try {
				return transformer.transform(type);
			} catch (TypeMappingException e) {
				throw TypeMappingException.ambiguousMemberType(member, declaringType, e);
			}
		}

		AnnotatedType transform(AnnotatedType type, Parameter parameter) {
			try {
				return transformer.transform(type);
			} catch (TypeMappingException e) {
				throw TypeMappingException.ambiguousParameterType(parameter.getDeclaringExecutable(), parameter, e);
			}
		}

		static Parameter getParameter(AnnotatedParameter ctorParam) {
			Executable constructor = (Executable) ctorParam.getOwner().getMember();
			return constructor.getParameters()[ctorParam.getIndex()];
		}
	}

}
