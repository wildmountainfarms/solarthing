package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * This class is typically used alongside a {@link java.util.NavigableSet} and its {@link java.util.NavigableSet#headSet(Object)} and {@link java.util.NavigableSet#tailSet(Object)}
 * methods. This allows the querying of data between certain time periods without needing a full instance of a {@link PacketGroup}.
 * <p>
 * A {@link PacketGroupNode} that has no packet group should never be stored in a data structure. Only use {@link PacketGroupNode(long)} when needing to query data.
 */
@NullMarked
public final class PacketGroupNode<T extends PacketGroup> implements Comparable<PacketGroupNode<T>> {
	private final long dateMillis;
	private final @Nullable T packetGroup;

	public PacketGroupNode(long dateMillis) {
		this.dateMillis = dateMillis;
		packetGroup = null;
	}

	public PacketGroupNode(T packetGroup) {
		dateMillis = packetGroup.getDateMillis();
		this.packetGroup = packetGroup;
	}

	@Override
	public int compareTo(PacketGroupNode<T> node) {
		return Long.compare(dateMillis, node.dateMillis);
	}

	public long getDateMillis() {
		return dateMillis;
	}

	public @Nullable T getPacketGroup() {
		return packetGroup;
	}

	public T expectPacketGroup() {
		return requireNonNull(packetGroup, () -> "expected packet group, but instead only dateMillis was provided: " + dateMillis);
	}
}
