package me.retrodaredevil.solarthing.graphql.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.Nullable;

import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class PacketNode<T> {
	private final T packet;
	private final long dateMillis;
	private final String sourceId;
	private final Integer fragmentId;

	public PacketNode(@NotNull T packet, long dateMillis, @NotNull String sourceId, @Nullable Integer fragmentId) {
		requireNonNull(this.packet = packet);
		this.dateMillis = dateMillis;
		requireNonNull(this.sourceId = sourceId);
		this.fragmentId = fragmentId;
	}

	@JsonProperty("packet")
	public @NotNull T getPacket() {
		return packet;
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
	public @Nullable Integer getFragmentId() {
		return fragmentId;
	}

	/**
	 * The GraphQL datasource takes all numeric values, so if you don't want it to automatically take the fragmentId and use it,
	 * you can call this instead to get a string representation of it.
	 * @return String representation of fragmentId
	 */
	@JsonProperty("fragmentIdString")
	public @NotNull String getFragmentIdString() {
		return "" + getFragmentId();
	}

	@Override
	public String toString() {
		return "PacketNode(" +
				"packet=" + packet +
				", dateMillis=" + dateMillis +
				", sourceId='" + sourceId + '\'' +
				", fragmentId=" + fragmentId +
				')';
	}
}
