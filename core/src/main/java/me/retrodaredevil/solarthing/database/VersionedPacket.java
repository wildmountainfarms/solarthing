package me.retrodaredevil.solarthing.database;

import static java.util.Objects.requireNonNull;

public class VersionedPacket<T> {
	private final T packet;
	private final UpdateToken updateToken;

	public VersionedPacket(T packet, UpdateToken updateToken) {
		requireNonNull(this.packet = packet);
		requireNonNull(this.updateToken = updateToken);
	}

	public T getPacket() {
		return packet;
	}

	public UpdateToken getUpdateToken() {
		return updateToken;
	}
}
