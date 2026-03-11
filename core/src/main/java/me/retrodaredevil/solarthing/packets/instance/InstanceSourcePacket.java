package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NonNull;

@JsonTypeName("SOURCE")
@JsonDeserialize(as = ImmutableInstanceSourcePacket.class)
@JsonExplicit
public interface InstanceSourcePacket extends InstancePacket, SourcedPacket {
	String DEFAULT_SOURCE_ID = "default";
	String UNUSED_SOURCE_ID = "<UNUSED SOURCE ID THAT WILL NEVER BE IN A PACKET>";

	@DefaultFinal
	@Override
	default @NonNull InstancePacketType getPacketType(){
		return InstancePacketType.SOURCE;
	}

	/**
	 * Should be serialized as "sourceId"
	 * @return The source id
	 */
	@JsonProperty("sourceId")
	@Override
	@NonNull String getSourceId();
}
