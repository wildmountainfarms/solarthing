package me.retrodaredevil.solarthing.rest.graphql.packets.nodes;


import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

/**
 * Represents a data point not associated with a particular device
 * @param <T> The type of the data
 */
@JsonExplicit
public final class SimpleNode<T> {
	private final T data;
	private final long dateMillis;

	public SimpleNode(@NonNull T data, long dateMillis) {
		this.data = requireNonNull(data);
		this.dateMillis = dateMillis;
	}

	@JsonProperty("data")
	public @NonNull T getData() {
		return data;
	}

	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
	}

}
