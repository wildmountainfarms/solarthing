package me.retrodaredevil.solarthing.type.alter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.PacketEntry;
import me.retrodaredevil.solarthing.packets.instance.SourcedPacket;

@JsonIgnoreProperties(value = { "_rev" }, allowGetters = true) // tightly coupled to CouchDB
@JsonDeserialize(as = ImmutableStoredAlterPacket.class)
@JsonExplicit
public interface StoredAlterPacket extends PacketEntry, SourcedPacket {

	@JsonProperty("_id") // tightly couple to CouchDB
	@Override
	String getDbId();

	// What is this useful for? Well, I'm not actually sure, but I figured I would put it here anyway. Maybe some client will find it useful in the future
	@JsonProperty("updatedDateMillis")
	long getUpdatedDateMillis();

	@JsonProperty("packet")
	@NotNull AlterPacket getPacket();

	@JsonProperty("sourceId")
	@Override
	@NotNull String getSourceId();
}
