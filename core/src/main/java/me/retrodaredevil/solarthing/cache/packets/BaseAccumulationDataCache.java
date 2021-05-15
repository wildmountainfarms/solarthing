package me.retrodaredevil.solarthing.cache.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.cache.IdentificationCacheData;

public abstract class BaseAccumulationDataCache implements IdentificationCacheData {
	private final Long firstDateMillis;
	private final Long lastDateMillis;

	private final Long unknownStartDateMillis;

	protected BaseAccumulationDataCache(Long firstDateMillis, Long lastDateMillis, Long unknownStartDateMillis) {
		this.firstDateMillis = firstDateMillis;
		this.lastDateMillis = lastDateMillis;
		this.unknownStartDateMillis = unknownStartDateMillis;

		if ((firstDateMillis == null) != (lastDateMillis == null)) {
			throw new IllegalStateException("firstDateMillis and lastDateMillis must either both be non-null or both must be null! firstDateMillis: " + firstDateMillis + " lastDateMillis: " + lastDateMillis);
		}
		if (unknownStartDateMillis != null && firstDateMillis == null) {
			throw new IllegalStateException("There cannot be an 'unknown' generation component if there is no data for this period! unknownGenerationStartDateMillis: " + unknownStartDateMillis);
		}
	}
	public boolean isUnknown() {
		return firstDateMillis == null;
	}

	@JsonProperty("firstDateMillis")
	public Long getFirstDateMillis() {
		return firstDateMillis;
	}

	@JsonProperty("lastDateMillis")
	public Long getLastDateMillis() {
		return lastDateMillis;
	}

	@JsonProperty("unknownStartDateMillis")
	public Long getUnknownStartDateMillis() {
		return unknownStartDateMillis;
	}
}
