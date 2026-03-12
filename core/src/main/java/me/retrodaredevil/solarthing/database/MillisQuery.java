package me.retrodaredevil.solarthing.database;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public final class MillisQuery {
	private final @Nullable Long startKey;
	private final @Nullable Long endKey;
	private final boolean inclusiveEnd;
	private final @Nullable Integer limit;
	private final boolean descending;

	public MillisQuery(@Nullable Long startKey, @Nullable Long endKey, boolean inclusiveEnd, @Nullable Integer limit, boolean descending) {
		this.startKey = startKey;
		this.endKey = endKey;
		this.inclusiveEnd = inclusiveEnd;
		this.limit = limit;
		this.descending = descending;
	}

	public @Nullable Long getStartKey() {
		return startKey;
	}

	public @Nullable Long getEndKey() {
		return endKey;
	}

	public boolean isInclusiveEnd() {
		return inclusiveEnd;
	}

	public @Nullable Integer getLimit() {
		return limit;
	}

	public boolean isDescending() {
		return descending;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MillisQuery that = (MillisQuery) o;
		return inclusiveEnd == that.inclusiveEnd && descending == that.descending && Objects.equals(startKey, that.startKey) && Objects.equals(endKey, that.endKey) && Objects.equals(limit, that.limit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startKey, endKey, inclusiveEnd, limit, descending);
	}
}
