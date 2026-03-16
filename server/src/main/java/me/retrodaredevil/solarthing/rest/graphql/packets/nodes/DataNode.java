package me.retrodaredevil.solarthing.rest.graphql.packets.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * Represents a single data point from some {@link Identifiable}
 * @param <T> The type of the data.
 */
@JsonExplicit
@NullMarked
public final class DataNode<T> implements Comparable<DataNode<?>> {
	private final T data;
	private final Identifiable identifiable;
	private final long dateMillis;
	private final String sourceId;
	private final int fragmentId;

	public DataNode(T data, Identifiable identifiable, long dateMillis, String sourceId, int fragmentId) {
		this.data = requireNonNull(data);
		this.identifiable = requireNonNull(identifiable);
		this.dateMillis = dateMillis;
		this.sourceId = requireNonNull(sourceId);
		this.fragmentId = fragmentId;
	}

	@JsonProperty("data")
	public @NonNull T getData() {
		return data;
	}
	@JsonProperty("identifiable")
	public @NonNull Identifiable getIdentifiable() {
		return identifiable;
	}

	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
	}

	@JsonProperty("sourceId")
	public @NonNull String getSourceId() {
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

	@Override
	public int compareTo(DataNode<?> dataNode) {
		int r = Long.compare(dateMillis, dataNode.dateMillis);
		if (r == 0) {
			return Integer.compare(identifiable.getIdentifier().hashCode(), dataNode.identifiable.getIdentifier().hashCode());
		}
		return r;
	}
}
