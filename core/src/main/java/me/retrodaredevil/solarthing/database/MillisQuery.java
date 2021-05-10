package me.retrodaredevil.solarthing.database;

public class MillisQuery {
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
}
