package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@NullMarked
class ImmutableStoredPacketGroup implements StoredPacketGroup {
	private final List<Packet> packets;
	private final long dateMillis;
	private final StoredIdentifier storedIdentifier;

	public ImmutableStoredPacketGroup(Collection<? extends Packet> packets, long dateMillis, StoredIdentifier storedIdentifier) {
		this.packets = Collections.unmodifiableList(new ArrayList<>(packets));
		this.dateMillis = dateMillis;
		this.storedIdentifier = requireNonNull(storedIdentifier);
	}

	@Override
	public List<Packet> getPackets() {
		return packets;
	}

	@Override
	public long getDateMillis() {
		return dateMillis;
	}

	// TODO remove NonNull
	@Override
	public @NonNull StoredIdentifier getStoredIdentifier() {
		return storedIdentifier;
	}
}
