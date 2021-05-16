package me.retrodaredevil.solarthing.cache.packets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.PacketEntry;
import me.retrodaredevil.solarthing.packets.instance.SourcedPacket;

import java.time.Duration;
import java.time.Instant;

@JsonIgnoreProperties(value = {"_id", "_rev"}, allowGetters = true) // tightly coupled to CouchDB, this is fine // this will be applied to subclasses
@JsonExplicit
public interface CacheDataPacket extends PacketEntry, SourcedPacket {

	@JsonProperty("_id") // tightly couple to CouchDB, but that's OK
	@Override
	String getDbId();

	@JsonProperty("sourceId")
	@Override
	@NotNull String getSourceId();

	@JsonProperty("cacheName")
	@NotNull String getCacheName();

	@JsonProperty("periodStartDateMillis")
	long getPeriodStartDateMillis();
	long getPeriodEndDateMillis();
	@JsonProperty("periodDurationMillis")
	long getPeriodDurationMillis();

	@NotNull Instant getPeriodStart();
	@NotNull Instant getPeriodEnd();
	@NotNull Duration getPeriodDuration();
}
