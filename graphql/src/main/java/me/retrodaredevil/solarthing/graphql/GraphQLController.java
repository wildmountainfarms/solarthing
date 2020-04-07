package me.retrodaredevil.solarthing.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphQLController {

	@Autowired
	private GraphQL graphQL;

	@RequestMapping(value = "/graphql")
	public Object graphql(@RequestBody String query) {
		ExecutionResult result = graphQL.execute(query);
		return result.getData();
	}
}
