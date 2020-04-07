package me.retrodaredevil.solarthing.graphql;

import com.nfl.glitr.registry.mutation.RelayMutationType;

public class CreateUserPayload extends RelayMutationType {
	private User user;

	public User getUser() {
		return user;
	}
	public CreateUserPayload setUser(User user) {
		this.user = user;
		return this;
	}
}
