package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

@JsonTypeName("SOURCE")
@JsonExplicit
public interface InstanceSourcePacket extends InstancePacket {
	String DEFAULT_SOURCE_ID = "default";

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
