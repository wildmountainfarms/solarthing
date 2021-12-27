package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import org.jetbrains.annotations.NotNull;

/**
 * This class is typically used alongside a {@link java.util.NavigableSet} and its {@link java.util.NavigableSet#headSet(Object)} and {@link java.util.NavigableSet#tailSet(Object)}
 * methods. This allows the querying of data between certain time periods without needing a full instance of a {@link PacketGroup}.
 */
public final class PacketGroupNode<T extends PacketGroup> implements Comparable<PacketGroupNode<T>> {
	private final long dateMillis;
	private final T packetGroup;

	public PacketGroupNode(long dateMillis) {
		this.dateMillis = dateMillis;
		packetGroup = null;
	}

	public PacketGroupNode(T packetGroup) {
		dateMillis = packetGroup.getDateMillis();
		this.packetGroup = packetGroup;
	}

	@Override
	public int compareTo(@NotNull PacketGroupNode<T> node) {
		return Long.compare(dateMillis, node.dateMillis);
	}

	public long getDateMillis() {
		return dateMillis;
	}

	public T getPacketGroup() {
		return packetGroup;
	}
}
