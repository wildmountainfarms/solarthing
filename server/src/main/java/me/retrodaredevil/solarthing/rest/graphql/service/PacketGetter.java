package me.retrodaredevil.solarthing.rest.graphql.service;

import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface PacketGetter {
	/**
	 *
	 * @return A mutable list of {@link PacketNode}s.
	 */
	<T> @NonNull List<@NonNull PacketNode<T>> getPackets(Class<T> clazz);
}
