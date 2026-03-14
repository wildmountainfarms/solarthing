package me.retrodaredevil.solarthing.type.cache.packets;

import me.retrodaredevil.solarthing.type.cache.CacheUtil;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

@NullMarked
public abstract class BaseCacheDataPacket implements CacheDataPacket {
	private final String sourceId;
	private final String cacheName;
	private final Instant periodStart;
	private final Instant periodEnd;
	private final Duration periodDuration;

	protected BaseCacheDataPacket(long periodStartDateMillis, long periodDurationMillis, String sourceId, String cacheName) {
		periodStart = Instant.ofEpochMilli(periodStartDateMillis);
		periodDuration = Duration.ofMillis(periodDurationMillis);
		this.sourceId = requireNonNull(sourceId);
		this.cacheName = requireNonNull(cacheName);

		periodEnd = Instant.ofEpochMilli(periodStartDateMillis + periodDurationMillis);
	}

	@Override
	public String getDbId() {
		return CacheUtil.getDocumentId(periodStart, periodDuration, sourceId, cacheName);
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public String getCacheName() {
		return cacheName;
	}

	@Override
	public Instant getPeriodStart() {
		return periodStart;
	}
	@Override
	public Instant getPeriodEnd() {
		return periodEnd;
	}
	@Override
	public Duration getPeriodDuration() {
		return periodDuration;
	}

	@Override
	public long getPeriodStartDateMillis() {
		return periodStart.toEpochMilli();
	}

	@Override
	public long getPeriodEndDateMillis() {
		return periodEnd.toEpochMilli();
	}

	@Override
	public long getPeriodDurationMillis() {
		return periodDuration.toMillis();
	}
}
