package me.retrodaredevil.solarthing.rest.graphql.packets.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

/**
 * Represents data without a timestamp
 */
public final class DataPoint<T> {
	private final T data;
	private final Identifiable identifiable;
	private final String sourceId;
	private final int fragmentId;

	public DataPoint(T data, Identifiable identifiable, String sourceId, int fragmentId) {
		this.data = data;
		this.identifiable = identifiable;
		this.sourceId = sourceId;
		this.fragmentId = fragmentId;
	}


	@JsonProperty("data")
	public @NotNull T getData() {
		return data;
	}

	@JsonProperty("identifiable")
	public @NotNull Identifiable getIdentifiable() {
		return identifiable;
	}
	@JsonProperty("sourceId")
	public @NotNull String getSourceId() {
		return sourceId;
	}

	@JsonProperty("fragmentId")
	public int getFragmentId() {
		return fragmentId;
	}
	@JsonProperty("fragmentIdString")
	public String getFragmentIdString() {
		return "" + fragmentId;
	}

}
