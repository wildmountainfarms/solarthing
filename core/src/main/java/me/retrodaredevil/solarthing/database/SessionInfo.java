package me.retrodaredevil.solarthing.database;

import org.jspecify.annotations.Nullable;

public final class SessionInfo {
	private final String username;

	public SessionInfo(String username) {
		this.username = username;
	}

	public @Nullable String getUsername() {
		return username;
	}
}
