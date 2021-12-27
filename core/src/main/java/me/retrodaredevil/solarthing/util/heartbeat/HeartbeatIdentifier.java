package me.retrodaredevil.solarthing.util.heartbeat;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A class to hold a heartbeat's identifier and the fragment ID it belongs to.
 * <p>
 * {@link #equals(Object)} and {@link #hashCode()} are implemented for use in datastructures relying on either method.
 */
public final class HeartbeatIdentifier {
	private final String identifier;
	private final int fragmentId;

	public HeartbeatIdentifier(@NotNull String identifier, int fragmentId) {
		requireNonNull(this.identifier = identifier);
		this.fragmentId = fragmentId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HeartbeatIdentifier that = (HeartbeatIdentifier) o;
		return fragmentId == that.fragmentId && identifier.equals(that.identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, fragmentId);
	}

	public @NotNull String getIdentifier() {
		return identifier;
	}

	public int getFragmentId() {
		return fragmentId;
	}
}
