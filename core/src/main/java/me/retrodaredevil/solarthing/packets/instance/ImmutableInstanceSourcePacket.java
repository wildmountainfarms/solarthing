package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

final class ImmutableInstanceSourcePacket implements InstanceSourcePacket {
	private final String sourceId;

	@JsonCreator
	ImmutableInstanceSourcePacket(@JsonProperty(value = "sourceId", required = true) String sourceId) {
		this.sourceId = requireNonNull(sourceId);
	}
	
	@Override
	public String getSourceId() {
		return sourceId;
	}
	
}
