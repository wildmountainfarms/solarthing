package me.retrodaredevil.solarthing.database;

import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class VersionedPacket<T> {
	private final T packet;
	private final UpdateToken updateToken;

	public VersionedPacket(T packet, UpdateToken updateToken) {
		this.packet = requireNonNull(packet);
		this.updateToken = requireNonNull(updateToken);
	}

	public T getPacket() {
		return packet;
	}

	public UpdateToken getUpdateToken() {
		return updateToken;
	}
}
