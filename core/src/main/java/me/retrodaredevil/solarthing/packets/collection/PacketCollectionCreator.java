package me.retrodaredevil.solarthing.packets.collection;

import org.jspecify.annotations.NonNull;

import java.time.Instant;

public interface PacketCollectionCreator {
	@NonNull PacketCollection create(@NonNull Instant now);
}
