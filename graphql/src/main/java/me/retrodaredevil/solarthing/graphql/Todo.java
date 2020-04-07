package me.retrodaredevil.solarthing.graphql;

import com.nfl.glitr.annotation.GlitrDescription;

@GlitrDescription("A Todo Object")
public class Todo {

	private String id;
	private String text;
	private boolean complete;
	private User user;


	public String getId() {
		return id;
	}

	public Todo setId(String id) {
		this.id = id;
		return this;
	}

	public String getText() {
		return text;
	}

	public Todo setText(String text) {
		this.text = text;
		return this;
	}

	public boolean isComplete() {
		return complete;
	}

	public Todo setComplete(boolean complete) {
		this.complete = complete;
		return this;
	}

	public User getUser() {
		return user;
	}

	public Todo setUser(User user) {
		this.user = user;
		return this;
	}
}
