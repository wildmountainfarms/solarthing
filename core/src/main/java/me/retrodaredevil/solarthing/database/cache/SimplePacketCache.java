package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class SimplePacketCache<T extends Packet> implements PacketCache<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimplePacketCache.class);

	private final long updatePeriodNanos;
	private final PacketSource<T> packetSource;
	private final boolean autoUpdate;

	private VersionedPacket<T> versionedPacket = null;
	private Long lastUpdateNanos = null;

	public SimplePacketCache(Duration updatePeriod, PacketSource<T> packetSource, boolean autoUpdate) {
		this.updatePeriodNanos = updatePeriod.toNanos();
		this.packetSource = packetSource;
		this.autoUpdate = autoUpdate;
	}

	private void updatePacket() {
		lastUpdateNanos = System.nanoTime();
		VersionedPacket<T> lastVersionedPacket = this.versionedPacket;
		final VersionedPacket<T> versionedPacket;
		try {
			versionedPacket = packetSource.query(lastVersionedPacket == null ? null : lastVersionedPacket.getUpdateToken());
		} catch (QueryException e) {
			LOGGER.error("Error getting authorization packet", e);
			return;
		}
		if (versionedPacket == null) {
			LOGGER.debug("Packet is the same since the last request");
			return;
		}
		this.versionedPacket = versionedPacket;
	}

	public void updateIfNeeded() {
		Long lastUpdateNanos = this.lastUpdateNanos;
		if (lastUpdateNanos == null || System.nanoTime() - lastUpdateNanos >= updatePeriodNanos) {
			updatePacket();
		}
	}

	@Override
	public @Nullable T getPacket() {
		if (autoUpdate) {
			updateIfNeeded();
		}
		VersionedPacket<T> versionedPacket = this.versionedPacket;
		if (versionedPacket == null) {
			return null;
		}
		return versionedPacket.getPacket();
	}

	public interface PacketSource<T extends Packet> {
		VersionedPacket<T> query(UpdateToken updateToken) throws QueryException;
	}
	public static class QueryException extends Exception {
		public QueryException() {
		}

		public QueryException(String message) {
			super(message);
		}

		public QueryException(String message, Throwable cause) {
			super(message, cause);
		}

		public QueryException(Throwable cause) {
			super(cause);
		}
	}
}
