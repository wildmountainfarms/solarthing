package me.retrodaredevil.solarthing.database;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class MillisQueryBuilder {
	private @Nullable Long startKey;
	private @Nullable Long endKey;
	private boolean inclusiveEnd = true;
	private @Nullable Integer limit;
	private boolean descending = false;

	public MillisQuery build() {
		return new MillisQuery(startKey, endKey, inclusiveEnd, limit, descending);
	}

	public MillisQueryBuilder startKey(@Nullable Long startKey) {
		this.startKey = startKey;
		return this;
	}
	public MillisQueryBuilder endKey(@Nullable Long endKey) {
		this.endKey = endKey;
		return this;
	}
	public MillisQueryBuilder inclusiveEnd(boolean inclusiveEnd) {
		this.inclusiveEnd = inclusiveEnd;
		return this;
	}
	public MillisQueryBuilder limit(@Nullable Integer limit) {
		this.limit = limit;
		return this;
	}
	public MillisQueryBuilder descending(boolean descending) {
		this.descending = descending;
		return this;
	}
}
