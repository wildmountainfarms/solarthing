package me.retrodaredevil.solarthing.graphql.service;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.graphql.packets.PacketNode;

import java.util.List;

public interface PacketGetter {
	<T> @NotNull List<@NotNull PacketNode<T>> getPackets(Class<T> clazz);
}
