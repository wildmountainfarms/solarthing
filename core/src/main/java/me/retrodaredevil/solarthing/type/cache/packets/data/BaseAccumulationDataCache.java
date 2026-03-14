package me.retrodaredevil.solarthing.type.cache.packets.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class BaseAccumulationDataCache implements IdentificationCacheData {
	private final @Nullable Long firstDateMillis;
	private final @Nullable Long lastDateMillis;

	private final @Nullable Long unknownStartDateMillis;

	protected BaseAccumulationDataCache(@Nullable Long firstDateMillis, @Nullable Long lastDateMillis, @Nullable Long unknownStartDateMillis) {
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

	/**
	 * Inverted version of {@link #isUnknown()}.
	 * This exists only because of the {@link EnsuresNonNull} annotation, which in theory should help with NullAway.
	 * Although the usage of {@link EnsuresNonNull} is mostly experimental at this point.
	 * <p>
	 * Consider removing this method in the future if we do not get value from {@link EnsuresNonNull}.
	 */
	@SuppressWarnings("NullAway") // Validation for lastDateMillis occurs within constructor - this is valid
	@EnsuresNonNull({"firstDateMillis", "lastDateMillis"})
	public boolean isKnown() {
		return firstDateMillis != null;
	}

	@JsonProperty("firstDateMillis")
	public @Nullable Long getFirstDateMillis() {
		return firstDateMillis;
	}

	@JsonProperty("lastDateMillis")
	public @Nullable Long getLastDateMillis() {
		return lastDateMillis;
	}

	@JsonProperty("unknownStartDateMillis")
	public @Nullable Long getUnknownStartDateMillis() {
		return unknownStartDateMillis;
	}
}
