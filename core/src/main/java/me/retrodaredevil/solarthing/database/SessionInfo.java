package me.retrodaredevil.solarthing.database;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class SessionInfo {
	private final @Nullable String username;

	public SessionInfo(@Nullable String username) {
		this.username = username;
	}

	public @Nullable String getUsername() {
		return username;
	}
}
