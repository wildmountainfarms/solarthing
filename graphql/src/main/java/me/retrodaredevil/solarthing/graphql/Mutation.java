package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfl.glitr.annotation.GlitrArgument;
import com.nfl.glitr.annotation.GlitrDescription;
import graphql.schema.DataFetchingEnvironment;

import java.util.Map;

@GlitrDescription("Where to persist something.")
public class Mutation {

	private ObjectMapper objectMapper = new ObjectMapper();

	@GlitrArgument(name = "input", type = CreateUserInput.class, nullability = GlitrArgument.nullability.NON_NULL)
	public CreateUserPayload getCreateUser(DataFetchingEnvironment env) {
		Map inputMap = env.getArgument("input");
		CreateUserInput input = objectMapper.convertValue(inputMap, CreateUserInput.class);

		User user = new User()
				.setId(input.getUser().getId())
				.setTodoList(input.getUser().getTodoList());

		CreateUserPayload payload = new CreateUserPayload();
		payload.setClientMutationId(input.getClientMutationId());
		payload.setUser(user);

		return payload;
	}

	@GlitrArgument(name = "input", type = CreateTodoInput.class, nullability = GlitrArgument.nullability.NON_NULL)
	public CreateTodoPayload getCreateTodo(DataFetchingEnvironment env) {
		Map inputMap = env.getArgument("input");
		CreateTodoInput input = objectMapper.convertValue(inputMap, CreateTodoInput.class);

		Todo todo = new Todo()
				.setId(input.getTodo().getId())
				.setText(input.getTodo().getText())
				.setComplete(input.getTodo().isComplete());

		CreateTodoPayload payload = new CreateTodoPayload();
		payload.setClientMutationId(input.getClientMutationId());
		payload.setTodo(todo);

		return payload;
	}
}
