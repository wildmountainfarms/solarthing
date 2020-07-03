package me.retrodaredevil.solarthing.graphql.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class DataNode<T> {
	private final T data;
	private final Identifiable identifiable;
	private final long dateMillis;
	private final String sourceId;
	private final int fragmentId;

	public DataNode(@NotNull T data, @NotNull Identifiable identifiable, long dateMillis, @NotNull String sourceId, int fragmentId) {
		this.fragmentId = fragmentId;
		requireNonNull(this.data = data);
		requireNonNull(this.identifiable = identifiable);
		this.dateMillis = dateMillis;
		requireNonNull(this.sourceId = sourceId);
	}

	@JsonProperty("data")
	public @NotNull T getData() {
		return data;
	}
	@JsonProperty("identifiable")
	public @NotNull Identifiable getIdentifiable() {
		return identifiable;
	}

	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
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
