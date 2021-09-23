package me.retrodaredevil.solarthing.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.DataSource;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * This class is meant to replace {@link DataSource} and can also directly be serialized to JSON
 * <p>
 * Represents a source from some packet stored in {@link SolarThingConstants#OPEN_DATABASE}. Note that {@link OpenSource}s should never be serialized in
 * the {@link SolarThingConstants#OPEN_DATABASE}, but they can be serialized in {@link SolarThingConstants#EVENT_DATABASE} to represent the cause/requester of some event
 */
@JsonExplicit
public final class OpenSource implements UniqueStringRepresentation {
	private final String sender;
	private final long dateMillis;
	private final OpenSourcePacket packet;
	/** Corresponds to {@link DataSource#getData()}*/
	private final String legacyData;

	@JsonCreator
	public OpenSource(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "dateMillis", required = true) long dateMillis,
			@JsonProperty(value = "packet", required = true) OpenSourcePacket packet,
			@JsonProperty(value = "legacyData", required = true) String legacyData) {
		requireNonNull(this.sender = sender);
		this.dateMillis = dateMillis;
		requireNonNull(this.packet = packet);
		requireNonNull(this.legacyData = legacyData);
	}

	public DataSource toDataSource() {
		return new DataSource(sender, dateMillis, legacyData);
	}

	@JsonProperty("sender")
	public @NotNull String getSender() {
		return sender;
	}
	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
	}

	@JsonProperty("packet")
	public @NotNull OpenSourcePacket getPacket() {
		return packet;
	}

	@JsonProperty("legacyData")
	public @NotNull String getLegacyData() {
		return legacyData;
	}

	@Override
	public @NotNull String getUniqueString() {
		return "OpenSource(" +
				"sender='" + sender + '\'' +
				", dateMillis=" + dateMillis +
				", packet=" + packet.getUniqueString() +
				", legacyData='" + legacyData + '\'' +
				')';
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OpenSource that = (OpenSource) o;
		return dateMillis == that.dateMillis && sender.equals(that.sender) && packet.equals(that.packet) && legacyData.equals(that.legacyData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sender, dateMillis, packet, legacyData);
	}
}
