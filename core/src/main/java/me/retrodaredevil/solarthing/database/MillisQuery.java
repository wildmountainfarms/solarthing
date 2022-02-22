package me.retrodaredevil.solarthing.database;

import java.util.Objects;

public final class MillisQuery {
	private final Long startKey;
	private final Long endKey;
	private final boolean inclusiveEnd;
	private final Integer limit;
	private final boolean descending;

	public MillisQuery(Long startKey, Long endKey, boolean inclusiveEnd, Integer limit, boolean descending) {
		this.startKey = startKey;
		this.endKey = endKey;
		this.inclusiveEnd = inclusiveEnd;
		this.limit = limit;
		this.descending = descending;
	}

	public Long getStartKey() {
		return startKey;
	}

	public Long getEndKey() {
		return endKey;
	}

	public boolean isInclusiveEnd() {
		return inclusiveEnd;
	}

	public Integer getLimit() {
		return limit;
	}

	public boolean isDescending() {
		return descending;
	}

	@Override
	public boolean equals(Object o) {
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
