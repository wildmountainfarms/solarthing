package me.retrodaredevil.solarthing.cache.packets;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.cache.CacheUtil;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

public abstract class BaseCacheDataPacket implements CacheDataPacket {
	private final String sourceId;
	private final String cacheName;
	private final Instant periodStart;
	private final Instant periodEnd;
	private final Duration periodDuration;

	protected BaseCacheDataPacket(long periodStartDateMillis, long periodDurationMillis, String sourceId, String cacheName) {
		periodStart = Instant.ofEpochMilli(periodStartDateMillis);
		periodDuration = Duration.ofMillis(periodDurationMillis);
		requireNonNull(this.sourceId = sourceId);
		requireNonNull(this.cacheName = cacheName);

		periodEnd = Instant.ofEpochMilli(periodStartDateMillis + periodDurationMillis);
	}

	@Override
	public String getDbId() {
		return CacheUtil.getDocumentId(periodStart, periodDuration, sourceId, cacheName);
	}

	@Override
	public @NotNull String getSourceId() {
		return sourceId;
	}

	@Override
	public @NotNull String getCacheName() {
		return cacheName;
	}

	@Override
	public @NotNull Instant getPeriodStart() {
		return periodStart;
	}
	@Override
	public @NotNull Instant getPeriodEnd() {
		return periodEnd;
	}
	@Override
	public @NotNull Duration getPeriodDuration() {
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
