package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import javax.validation.constraints.NotNull;

@JsonTypeName("SOURCE")
@JsonDeserialize(as = ImmutableInstanceSourcePacket.class)
@JsonExplicit
public interface InstanceSourcePacket extends InstancePacket {
	String DEFAULT_SOURCE_ID = "default";

	@NotNull
    @Override
	default InstancePacketType getPacketType(){
		return InstancePacketType.SOURCE;
	}

	/**
	 * Should be serialized as "sourceId"
	 * @return The source id
	 */
	@JsonProperty("sourceId")
	String getSourceId();
}
