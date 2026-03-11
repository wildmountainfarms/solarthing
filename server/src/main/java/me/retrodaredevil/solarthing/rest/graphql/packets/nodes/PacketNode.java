package me.retrodaredevil.solarthing.rest.graphql.packets.nodes;

import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class PacketNode<T> implements SimplePacketNode {
	private final T packet;
	private final long dateMillis;
	private final String sourceId;
	private final int fragmentId;

	public PacketNode(@NotNull T packet, long dateMillis, @NotNull String sourceId, int fragmentId) {
		this.packet = requireNonNull(packet);
		this.dateMillis = dateMillis;
		this.sourceId = requireNonNull(sourceId);
		this.fragmentId = fragmentId;
	}

	@Override
	public @NotNull T getPacket() {
		return packet;
	}

	@Override
	public long getDateMillis() {
		return dateMillis;
	}

	@Override
	public @NotNull String getSourceId() {
		return sourceId;
	}

	@Override
	public int getFragmentId() {
		return fragmentId;
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
