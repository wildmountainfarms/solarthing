package me.retrodaredevil.solarthing.rest.graphql;

import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import graphql.language.SourceLocation;
import me.retrodaredevil.solarthing.rest.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolarThingExceptionHandler implements DataFetcherExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolarThingExceptionHandler.class);

	/*
	It's worth nothing that SimpleDataFetcherExceptionHandler appends a "notprivacysafe." in front of the full class name for the logger.
	We aren't going to do that here, as there's no private information, but it's just worth noting the difference here.
	 */

	@Override
	public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {

		Throwable exception = handlerParameters.getException();
		SourceLocation sourceLocation = handlerParameters.getSourceLocation();
		ResultPath path = handlerParameters.getPath();

		if (exception instanceof DatabaseException) { // this is the most common exception, usually caused by a timeout
			LOGGER.info("Got database exception", exception);
		} else {
			LOGGER.warn("Got uncommon exception", exception);
		}

		ExceptionWhileDataFetching error = new ExceptionWhileDataFetching(path, exception, sourceLocation);
		return DataFetcherExceptionHandlerResult.newResult().error(error).build();
	}
}
