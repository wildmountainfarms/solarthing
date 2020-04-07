package me.retrodaredevil.solarthing.graphql;

import com.nfl.glitr.annotation.GlitrDescription;

import java.util.List;

@GlitrDescription("A User Object")
public class User {

	private String id;
	private List<Todo> todoList;


	public String getId() {
		return id;
	}

	public User setId(String id) {
		this.id = id;
		return this;
	}

	public List<Todo> getTodoList() {
		return todoList;
	}

	public User setTodoList(List<Todo> todoList) {
		this.todoList = todoList;
		return this;
	}
}
