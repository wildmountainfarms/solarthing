package me.retrodaredevil.solarthing.rest.graphql;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import io.leangen.graphql.generator.BuildContext;
import io.leangen.graphql.generator.OperationMapper;
import io.leangen.graphql.generator.mapping.common.NonNullMapper;
import io.leangen.graphql.metadata.DirectiveArgument;
import io.leangen.graphql.metadata.InputField;
import io.leangen.graphql.metadata.Operation;
import io.leangen.graphql.metadata.OperationArgument;
import io.leangen.graphql.metadata.TypedElement;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;

/**
 * Maps unannotated types to GraphQL non-null types, including collection elements.
 * Explicit JSpecify {@link Nullable} annotations opt out at each type position.
 * <p>
 * Relates to
 * https://github.com/wildmountainfarms/solarthing/issues/234
 * and
 * github.com/leangen/graphql-spqr/issues/334
 */
@NullMarked
public class DefaultNonNullMapper extends NonNullMapper {

	// TODO consider actually introspecting further to determine if a parameter/method is in a NullMarked scope

	@Override
	public boolean supports(AnnotatedElement element, AnnotatedType type) {
		return !type.isAnnotationPresent(Nullable.class);
	}

	private static GraphQLType applyNullability(GraphQLType type, TypedElement element) {
		if (element.isAnnotationPresentAnywhere(Nullable.class)) {
			return type instanceof GraphQLNonNull ? ((GraphQLNonNull) type).getWrappedType() : type;
		}
		return type instanceof GraphQLNonNull ? type : GraphQLNonNull.nonNull(type);
	}

	@Override
	public GraphQLFieldDefinition transformField(GraphQLFieldDefinition field, Operation operation, OperationMapper operationMapper, BuildContext buildContext) {
		return field.transform(builder -> builder.type((GraphQLOutputType) applyNullability(field.getType(), operation.getTypedElement())));
	}

	@Override
	public GraphQLInputObjectField transformInputField(GraphQLInputObjectField field, InputField inputField, OperationMapper operationMapper, BuildContext buildContext) {
		return field.transform(builder -> builder.type((GraphQLInputType) applyNullability(field.getType(), inputField.getTypedElement())));
	}

	@Override
	public GraphQLArgument transformArgument(GraphQLArgument argument, OperationArgument operationArgument, OperationMapper operationMapper, BuildContext buildContext) {
		return argument.transform(builder -> builder.type((GraphQLInputType) applyNullability(argument.getType(), operationArgument.getTypedElement())));
	}

	@Override
	public GraphQLArgument transformArgument(GraphQLArgument argument, DirectiveArgument directiveArgument, OperationMapper operationMapper, BuildContext buildContext) {
		return argument.transform(builder -> builder.type((GraphQLInputType) applyNullability(argument.getType(), directiveArgument.getTypedElement())));
	}
}
