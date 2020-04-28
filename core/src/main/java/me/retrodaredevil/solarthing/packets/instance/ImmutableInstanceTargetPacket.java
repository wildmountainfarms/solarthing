package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
final class ImmutableInstanceTargetPacket implements InstanceTargetPacket {
	private final String targetId;

	@JsonCreator
	ImmutableInstanceTargetPacket(@JsonProperty(value = "targetId", required = true) String targetId) {
		this.targetId = targetId;
	}

	@Override
	public String getTargetId() {
		return targetId;
	}
}
