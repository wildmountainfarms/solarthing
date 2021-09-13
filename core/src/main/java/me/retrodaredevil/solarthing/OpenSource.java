package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.Packet;

import static java.util.Objects.requireNonNull;

/**
 * This class is meant to replace {@link DataSource} and can also directly be serialized to JSON
 * <p>
 * Represents a source from some packet stored in {@link SolarThingConstants#OPEN_DATABASE}. Note that {@link OpenSource}s should never be serialized in
 * the {@link SolarThingConstants#OPEN_DATABASE}, but they can be serialized in {@link SolarThingConstants#EVENT_DATABASE} to represent the cause/requester of some event
 */
@JsonExplicit
public final class OpenSource {
	private final String sender;
	private final long dateMillis;
	private final Packet packet;
	/** Corresponds to {@link DataSource#getData()}*/
	private final String legacyData;

	public OpenSource(String sender, long dateMillis, Packet packet, String legacyData) {
		requireNonNull(this.sender = sender);
		this.dateMillis = dateMillis;
		this.packet = packet;
		this.legacyData = legacyData;
	}

	public DataSource toDataSource() {
		return new DataSource(sender, dateMillis, legacyData);
	}

	@JsonProperty("sender")
	public String getSender() {
		return sender;
	}
	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
	}

	@JsonProperty("packet")
	public Packet getPacket() {
		return packet;
	}
}
