package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.annotations.Nullable;

public final class SessionInfo {
	private final String username;

	public SessionInfo(String username) {
		this.username = username;
	}

	public @Nullable String getUsername() {
		return username;
	}
}
