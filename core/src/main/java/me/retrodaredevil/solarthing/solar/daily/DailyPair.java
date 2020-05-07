package me.retrodaredevil.solarthing.solar.daily;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.solar.common.DailyData;

import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class DailyPair<T extends DailyData & Packet> {
	private final TimestampedPacket<T> startPacket;
	private final TimestampedPacket<T> latestPacket;
	private final StartPacketType startPacketType;

	public DailyPair(TimestampedPacket<T> startPacket, TimestampedPacket<T> latestPacket, StartPacketType startPacketType) {
		requireNonNull(this.startPacket = startPacket);
		requireNonNull(this.latestPacket = latestPacket);
		requireNonNull(this.startPacketType = startPacketType);
	}
	public @NotNull TimestampedPacket<T> getStartPacket() {
		return startPacket;
	}
	public @NotNull TimestampedPacket<T> getLatestPacket() {
		return latestPacket;
	}
	public @NotNull StartPacketType getStartPacketType() {
		return startPacketType;
	}

	public enum StartPacketType {
		/**
		 * Represents the packet from the start of the day, usually with a non-zero accumulation
		 */
		CUT_OFF,
		/**
		 * Represents the first packet that can essentially be 0. This is used after a reset in the middle of a day.
		 * <p>
		 * Normally the accumulated values on the start packet will always be 0, but even if they are not, you should treat them as zero
		 */
		FIRST_AFTER_RESET,
		/**
		 * Represents a non-cut off packet that might not have happened after a reset, but can be assumed that the accumulation was not yesterday
		 */
		MIDDLE_OF_DAY_CONNECT
	}
}
