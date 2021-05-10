package me.retrodaredevil.solarthing.database;

public class MillisQueryBuilder {
	private Long startKey;
	private Long endKey;
	private boolean inclusiveEnd = true;
	private Integer limit;
	private boolean descending = false;

	public MillisQuery build() {
		return new MillisQuery(startKey, endKey, inclusiveEnd, limit, descending);
	}

	public MillisQueryBuilder startKey(Long startKey) {
		this.startKey = startKey;
		return this;
	}
	public MillisQueryBuilder endKey(Long endKey) {
		this.endKey = endKey;
		return this;
	}
	public MillisQueryBuilder inclusiveEnd(boolean inclusiveEnd) {
		this.inclusiveEnd = inclusiveEnd;
		return this;
	}
	public MillisQueryBuilder limit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public MillisQueryBuilder descending(boolean descending) {
		this.descending = descending;
		return this;
	}
}
