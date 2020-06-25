package me.retrodaredevil.solarthing.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.PacketEntry;

import java.util.List;

@JsonIgnoreProperties(value = { "_id", "_rev" }, allowGetters = true)
public class RootMetaPacket implements PacketEntry {
	private final List<TimedMetaCollection> meta;

	@JsonCreator
	public RootMetaPacket(@JsonProperty("meta") List<TimedMetaCollection> meta) {
		this.meta = meta;
	}

	@Override
	public String getDbId() {
		return "meta";
	}

	@JsonProperty("meta")
	public List<TimedMetaCollection> getMeta() {
		return meta;
	}
}
