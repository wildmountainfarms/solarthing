package me.retrodaredevil.solarthing.graphql;

import com.nfl.glitr.registry.mutation.RelayMutationType;

public class CreateTodoPayload extends RelayMutationType {

	private Todo todo;

	public Todo getTodo() {
		return todo;
	}

	public CreateTodoPayload setTodo(Todo todo) {
		this.todo = todo;
		return this;
	}
}
