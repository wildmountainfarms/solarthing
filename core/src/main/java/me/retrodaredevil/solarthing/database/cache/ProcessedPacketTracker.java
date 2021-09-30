package me.retrodaredevil.solarthing.database.cache;

import me.retrodaredevil.solarthing.packets.collection.DateMillisStoredIdentifier;
import me.retrodaredevil.solarthing.packets.collection.StoredIdentifier;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.util.TimeRange;

import java.time.Duration;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ProcessedPacketTracker {
	private static final long BUFFER_MILLIS = Duration.ofMinutes(5).toMillis();
	private final DatabaseCache databaseCache;

	private final NavigableSet<StoredIdentifier> processed = new TreeSet<>();

	public ProcessedPacketTracker(DatabaseCache databaseCache) {
		this.databaseCache = databaseCache;
	}

	public List<StoredPacketGroup> getUnprocessedPackets(long afterDateMillis) {
		// Use buffer millis here because of the possibility of the clock jumping back and then an already processed packet being returned
		//   With a perfect clock (and with almost all clocks), this will never happen
		processed.headSet(new DateMillisStoredIdentifier(afterDateMillis - BUFFER_MILLIS), false).clear();

		List<StoredPacketGroup> r = databaseCache.createCachedPacketsInRangeStream(TimeRange.createAfter(afterDateMillis), false)
				.filter(storedPacketGroup -> !processed.contains(storedPacketGroup.getStoredIdentifier()))
				.collect(Collectors.toList());
		r.stream().map(StoredPacketGroup::getStoredIdentifier).collect(Collectors.toCollection(() -> processed));
		return r;
	}
}
