package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import me.retrodaredevil.solarthing.annotations.NotNull;

@Deprecated // never used. Maybe used in the future
@JsonTypeName("TARGET")
@JsonDeserialize(as = ImmutableInstanceTargetPacket.class)
@JsonExplicit
public interface InstanceTargetPacket extends InstancePacket {

	@NotNull
    @Override
	default InstancePacketType getPacketType() {
		return InstancePacketType.TARGET;
	}

	/**
	 * Should be serialized as "targetId"
	 * @return The target id
	 */
	@JsonProperty("targetId")
	String getTargetId();
}
