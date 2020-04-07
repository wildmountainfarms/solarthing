package me.retrodaredevil.solarthing.graphql;


import com.nfl.glitr.annotation.GlitrDescription;
import graphql.schema.DataFetchingEnvironment;

@GlitrDescription("Where it all begins.")
public class Root {

	public Todo getTodo(DataFetchingEnvironment env) {
		return new Todo().setId("first-todo");
	}

	public User getUser(DataFetchingEnvironment env) {
		return new User().setId("first-user");
	}
}


