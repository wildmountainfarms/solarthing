package me.retrodaredevil.solarthing.solar.accumulation;

import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class AccumulationPair<T extends DailyData> {
	private final TimestampedPacket<T> startPacket;
	private final TimestampedPacket<T> latestPacket;
	private final StartPacketType startPacketType;

	public AccumulationPair(TimestampedPacket<T> startPacket, TimestampedPacket<T> latestPacket, StartPacketType startPacketType) {
		this.startPacket = requireNonNull(startPacket);
		this.latestPacket = requireNonNull(latestPacket);
		this.startPacketType = requireNonNull(startPacketType);
	}
	public TimestampedPacket<T> getStartPacket() {
		return startPacket;
	}
	public TimestampedPacket<T> getLatestPacket() {
		return latestPacket;
	}
	public StartPacketType getStartPacketType() {
		return startPacketType;
	}

	public enum StartPacketType {
		/**
		 * Represents the packet from the start of the day, usually with a non-zero accumulation.
		 * <p>
		 * This means that current accumulated data for the given day should be determined by (end - first)
		 */
		CUT_OFF,
		/**
		 * Represents the first packet that can essentially be 0. This is used after a reset in the middle of a day.
		 * <p>
		 * Normally the accumulated values on the start packet will always be 0, but even if they are not, you should treat them as zero.
		 * <p>
		 * This means that data should be determined by (end).
		 */
		FIRST_AFTER_RESET,
		/**
		 * Represents a non-cut off packet that might not have happened after a reset, but can be assumed that the accumulation was not from yesterday.
		 * <p>
		 * This is similar to {@link #CUT_OFF} (since data is likely non-zero), but means that some condition has been met that makes SolarThing
		 * believe that the first packet has its accumulated data from today and not yesterday.
		 * <p>
		 * This means that current accumulated data for the given day should be determined by (end). If you incorrectly use (end - first), you are likely
		 * disregarding data from the given data from (first).
		 */
		MIDDLE_OF_DAY_CONNECT
	}
}
