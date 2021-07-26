package me.retrodaredevil.solarthing.rest.exceptions;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.List;

/**
 * This exception is for something that is possible for an API or database to give a response, but is unlikely.
 */
public class UnexpectedResponseException extends RestException implements GraphQLError {
	public UnexpectedResponseException() {
	}

	public UnexpectedResponseException(String message) {
		super(message);
	}

	public UnexpectedResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedResponseException(Throwable cause) {
		super(cause);
	}

	public UnexpectedResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
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
