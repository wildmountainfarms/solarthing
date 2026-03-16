package me.retrodaredevil.solarthing.rest.graphql.service;

import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface PacketGetter {
	/**
	 *
	 * @return A mutable list of {@link PacketNode}s.
	 */
	<T> List<PacketNode<T>> getPackets(Class<T> clazz);
}
