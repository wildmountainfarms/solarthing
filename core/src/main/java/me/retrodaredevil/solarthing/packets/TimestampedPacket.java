package me.retrodaredevil.solarthing.packets;

import static java.util.Objects.requireNonNull;

public final class TimestampedPacket<T extends Packet> {
	private final T packet;
	private final long dateMillis;

	public TimestampedPacket(T packet, long dateMillis) {
		requireNonNull(this.packet = packet);
		this.dateMillis = dateMillis;
	}

	public T getPacket() {
		return packet;
	}

	public long getDateMillis() {
		return dateMillis;
	}
}
