package me.retrodaredevil.solarthing.rest.exceptions;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;

/**
 * This exception is for something that is possible for an API or database to give a response, but is unlikely.
 */
@NullMarked
public class UnexpectedResponseException extends RestException implements GraphQLError {
	public UnexpectedResponseException(String message) {
		super(message);
	}

	public UnexpectedResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public List<SourceLocation> getLocations() {
		return Collections.emptyList();
	}

	@Override
	public ErrorClassification getErrorType() {
		return ErrorType.DataFetchingException;
	}
}
