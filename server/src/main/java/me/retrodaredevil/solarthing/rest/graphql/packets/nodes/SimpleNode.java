package me.retrodaredevil.solarthing.rest.graphql.packets.nodes;


import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Represents a data point not associated with a particular device
 * @param <T> The type of the data
 */
@JsonExplicit
public final class SimpleNode<T> {
	private final T data;
	private final long dateMillis;

	public SimpleNode(@NotNull T data, long dateMillis) {
		requireNonNull(this.data = data);
		this.dateMillis = dateMillis;
	}

	@JsonProperty("data")
	public @NotNull T getData() {
		return data;
	}

	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
	}

}
