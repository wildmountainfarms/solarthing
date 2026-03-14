package me.retrodaredevil.solarthing.type.cache.packets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.PacketEntry;
import me.retrodaredevil.solarthing.packets.instance.SourcedPacket;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.time.Instant;

@JsonIgnoreProperties(value = {"_id", "_rev"}, allowGetters = true) // tightly coupled to CouchDB, this is fine // this will be applied to subclasses
@JsonExplicit
@NullMarked
public interface CacheDataPacket extends PacketEntry, SourcedPacket {

	@JsonProperty("_id") // tightly couple to CouchDB, but that's OK
	@Override
	String getDbId();

	// TODO remove NonNull
	@JsonProperty("sourceId")
	@Override
	@NonNull String getSourceId();

	// TODO remove NonNull
	@JsonProperty("cacheName")
	@NonNull String getCacheName();

	@JsonProperty("periodStartDateMillis")
	long getPeriodStartDateMillis();
	long getPeriodEndDateMillis();
	@JsonProperty("periodDurationMillis")
	long getPeriodDurationMillis();

	Instant getPeriodStart();
	Instant getPeriodEnd();
	Duration getPeriodDuration();

	/**
	 * Combines this packet with a future packet. Note this will only work as expected if these packets are back to back
	 * @param futurePacket The {@link CacheDataPacket} of the same type as this
	 * @return The combined packet
	 */
	CacheDataPacket combine(CacheDataPacket futurePacket);
}
