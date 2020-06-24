package me.retrodaredevil.solarthing.graphql.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

public interface SimplePacketNode {
	@JsonProperty("packet")
	@NotNull Object getPacket();

	@JsonProperty("dateMillis")
	long getDateMillis();

	@JsonProperty("sourceId")
	@NotNull String getSourceId();

	@JsonProperty("fragmentId")
	int getFragmentId();
}
