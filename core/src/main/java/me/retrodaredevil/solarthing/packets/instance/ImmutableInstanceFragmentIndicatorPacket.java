package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class ImmutableInstanceFragmentIndicatorPacket implements InstanceFragmentIndicatorPacket {
	private final int fragmentId;

	@JsonCreator
	ImmutableInstanceFragmentIndicatorPacket(@JsonProperty(value = "fragmentId", required = true) int fragmentId) {
		this.fragmentId = fragmentId;
	}

	@Override
	public int getFragmentId() {
		return fragmentId;
	}

}
