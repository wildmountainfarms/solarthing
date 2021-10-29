package me.retrodaredevil.solarthing.packets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.retrodaredevil.solarthing.annotations.Nullable;

/**
 * Any packet inheriting this class is usually a packet that might have changed how fields are serialized, or maybe some underlying functionality
 * in how the data is generated.
 * <p>
 * A packet's version helps distinguish between whatever dissimilarity there may be in previous versions.
 * <p>
 * Note that if you try to serialize a packet with an older packet version, the serialization WILL NOT try to serialize it like it did in a previous
 * SolarThing version. You should never expect to be able to serialize a packet again and also keep the version the same (although it might be fine with
 * some implementations).
 */
@JsonPropertyOrder({"packetType", "packetVersion"})
public interface VersionedPacket extends DocumentedPacket {
	// TODO rename this class to something else

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("packetVersion")
	@Nullable Integer getPacketVersion();
}
