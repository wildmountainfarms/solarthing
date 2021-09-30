package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.util.TimeRange;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a database cache.
 * <p>
 * Note: Some of these methods return {@link Stream}s. This helps avoid additional overhead of creating a new list, but the returned stream should be used immediately,
 * or not at all. The returned {@link Stream} is usually backed by internal data depending on the implementation, meaning that the result of consuming the stream
 * may be different depending on when it is consumed. This is most important when using this in a multi-threaded environment. It is likely that the
 * implementation of this is not thread safe, and attempting to consume a returned stream while another thread is possibily mutating this implementation is
 * a big no-no. Just something to be aware of.
 * <p>
 * It's also worth noting that even though some methods return lists, that does not mean the implementation is thread safe, especially because it's likely
 * that the implementation of the methods returning a list aren't synchronized (The default implementation of them aren't).
 * <p>
 * TDLR; The implementation probably isn't thread safe.
 */
public interface DatabaseCache {
	Stream<StoredPacketGroup> createCachedPacketsInRangeStream(TimeRange timeRange, boolean descending);
	default Stream<StoredPacketGroup> createAllCachedPacketsStream(boolean descending) {
		return createCachedPacketsInRangeStream(TimeRange.ALWAYS, descending);
	}

	default List<StoredPacketGroup> getCachedPacketsInRange(TimeRange timeRange, boolean descending) {
		return createCachedPacketsInRangeStream(timeRange, descending).collect(Collectors.toList());
	}

	default List<StoredPacketGroup> getAllCachedPackets(boolean descending) {
		return getCachedPacketsInRange(TimeRange.ALWAYS, descending);
	}
	default List<StoredPacketGroup> getAllCachedPackets() {
		return getAllCachedPackets(false);
	}

}
