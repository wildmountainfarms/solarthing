package me.retrodaredevil.solarthing.rest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.execution.DefaultExecutionGraphQlService;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

@Configuration
public class WebConfiguration {

	@Bean
	public HandlerMapping catchAllHandlerMapping(ApplicationContext applicationContext) {
		// related to https://stackoverflow.com/a/42998817/5434860
		// We have to do it this way, so we can set the order so this has the lowest precedence
		var registry = new ViewControllerRegistry(applicationContext) {
			@Override
			public SimpleUrlHandlerMapping buildHandlerMapping() {
				return super.buildHandlerMapping();
			}
		};
		registry.setOrder(5);
		registry.addViewController("/{spring:[a-zA-Z0-9-_]+}")
				.setViewName("forward:/");
		return registry.buildHandlerMapping();
	}

	@Bean
	public RouterFunction<ServerResponse> graphQLRouterFunction(GraphQlSource graphQlSource) {
		ExecutionGraphQlService executionGraphQlService = new DefaultExecutionGraphQlService(graphQlSource);
		GraphQlHttpHandler handler = new GraphQlHttpHandler(
				WebGraphQlHandler.builder(executionGraphQlService)
						.build()
		);
		return RouterFunctions.route(RequestPredicates.path("/graphql"), handler::handleRequest);
//		return RouterFunctions.route()
//				.POST("/graphql", RequestPredicates.contentType(MediaType.APPLICATION_JSON), handler::handleRequest)
//				.build()
//				;
	}
}
