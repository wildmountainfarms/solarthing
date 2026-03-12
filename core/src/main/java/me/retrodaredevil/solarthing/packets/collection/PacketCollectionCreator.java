package me.retrodaredevil.solarthing.packets.collection;

import org.jspecify.annotations.NullMarked;

import java.time.Instant;

@NullMarked
public interface PacketCollectionCreator {
	PacketCollection create(Instant now);
}
