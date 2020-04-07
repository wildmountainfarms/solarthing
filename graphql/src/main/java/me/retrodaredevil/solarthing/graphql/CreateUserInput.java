package me.retrodaredevil.solarthing.graphql;

import com.nfl.glitr.registry.mutation.RelayMutationType;

import java.util.List;

public class CreateUserInput extends RelayMutationType {

	private UserMutation userMutation;

	public UserMutation getUser() {
		return userMutation;
	}

	public CreateUserInput setUser(UserMutation userMutation) {
		this.userMutation = userMutation;
		return this;
	}

	public static class UserMutation {

		private String id;
		private List<Todo> todoList;


		public String getId() {
			return id;
		}

		public UserMutation setId(String id) {
			this.id = id;
			return this;
		}

		public List<Todo> getTodoList() {
			return todoList;
		}

		public UserMutation setTodoList(List<Todo> todoList) {
			this.todoList = todoList;
			return this;
		}
	}
}
