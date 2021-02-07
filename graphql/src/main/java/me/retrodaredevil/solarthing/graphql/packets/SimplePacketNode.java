package me.retrodaredevil.solarthing.graphql.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import me.retrodaredevil.solarthing.annotations.NotNull;

public interface SimplePacketNode {
	@JsonProperty("packet")
	@NotNull Object getPacket();

	@JsonProperty("dateMillis")
	long getDateMillis();

	@JsonProperty("sourceId")
	@JsonPropertyDescription("The Source ID the packet was from")
	@NotNull String getSourceId();

	@JsonProperty("fragmentId")
	@JsonPropertyDescription("The fragment the packet was from")
	int getFragmentId();

	/**
	 * The GraphQL datasource takes all numeric values, so if you don't want it to automatically take the fragmentId and use it,
	 * you can call this instead to get a string representation of it.
	 * @return String representation of fragmentId
	 */
	@JsonProperty("fragmentIdString")
	default @NotNull String getFragmentIdString() {
		return "" + getFragmentId();
	}
}
