package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonTypeName("SOURCE")
@JsonDeserialize(as = ImmutableInstanceSourcePacket.class)
@JsonExplicit
public interface InstanceSourcePacket extends InstancePacket, SourcedPacket {
	String DEFAULT_SOURCE_ID = "default";
	String UNUSED_SOURCE_ID = "<UNUSED SOURCE ID THAT WILL NEVER BE IN A PACKET>";

	@Override
	default @NotNull InstancePacketType getPacketType(){
		return InstancePacketType.SOURCE;
	}

	/**
	 * Should be serialized as "sourceId"
	 * @return The source id
	 */
	@JsonProperty("sourceId")
	@Override
    @NotNull String getSourceId();
}
