package me.retrodaredevil.solarthing.packets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.retrodaredevil.solarthing.annotations.Nullable;

@JsonPropertyOrder({"packetType", "packetVersion"})
public interface VersionedPacket extends DocumentedPacket {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("packetVersion")
	@Nullable Integer getPacketVersion();
}
