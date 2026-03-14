package me.retrodaredevil.solarthing.type.cache;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.time.Instant;

@UtilityClass
@NullMarked
public final class CacheUtil {
	private CacheUtil() { throw new UnsupportedOperationException(); }

	public static String getDocumentId(Instant periodStart, Duration periodDuration, String sourceId, String cacheName) {
		return "cache_" + periodStart + "_" + periodDuration + "_" + sourceId + "_" + cacheName;
	}
}
