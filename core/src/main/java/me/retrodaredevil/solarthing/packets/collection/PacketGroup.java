package me.retrodaredevil.solarthing.packets.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;

@JsonExplicit
@NullMarked
public interface PacketGroup {
	/**
	 * Should be serialized as "packets"
	 * @return An unmodifiable list of packets
	 */
	@JsonProperty("packets")
	List<? extends Packet> getPackets();

	/**
	 * Should be serialized as "dateMillis"
	 * @return The date this packet collection was created in milliseconds (UTC)
	 */
	@JsonProperty("dateMillis")
	long getDateMillis();

	default Instant getTimestamp() {
		return Instant.ofEpochMilli(getDateMillis());
	}

	/**
	 * @param packet The packet to get the date millis from
	 * @return The date millis of the specific packet or null
	 * @throws java.util.NoSuchElementException If {@code packet} is not in {@link #getPackets()}. Optional to be thrown
	 */
	default @Nullable Long getDateMillis(Packet packet){
		return null;
	}
	default long getDateMillisOrKnown(Packet packet) {
		Long dateMillis = getDateMillis(packet);
		if (dateMillis == null) {
			return getDateMillis();
		}
		return dateMillis;
	}
}
