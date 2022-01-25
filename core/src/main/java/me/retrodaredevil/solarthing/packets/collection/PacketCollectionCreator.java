package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.time.Instant;

public interface PacketCollectionCreator {
	@NotNull PacketCollection create(@NotNull Instant now);
}
