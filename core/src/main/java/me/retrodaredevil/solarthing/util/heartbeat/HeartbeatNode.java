package me.retrodaredevil.solarthing.util.heartbeat;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatPacket;

import static java.util.Objects.requireNonNull;

/**
 * A simple class to hold a dateMillis and heartbeatPacket
 */
public final class HeartbeatNode {
	private final long dateMillis;
	private final HeartbeatPacket heartbeatPacket;

	public HeartbeatNode(long dateMillis, HeartbeatPacket heartbeatPacket) {
		this.dateMillis = dateMillis;
		requireNonNull(this.heartbeatPacket = heartbeatPacket);
	}

	public long getDateMillis() {
		return dateMillis;
	}

	public @NotNull HeartbeatPacket getHeartbeatPacket() {
		return heartbeatPacket;
	}
}
