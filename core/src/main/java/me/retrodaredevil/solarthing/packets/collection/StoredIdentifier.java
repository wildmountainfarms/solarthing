package me.retrodaredevil.solarthing.packets.collection;

import org.jetbrains.annotations.NotNull;

public abstract class StoredIdentifier implements Comparable<StoredIdentifier> {
	protected final long dateMillis;

	protected StoredIdentifier(long dateMillis) {
		this.dateMillis = dateMillis;
	}
	@Override
	public abstract boolean equals(Object o);
	@Override
	public abstract int hashCode();
	@Override
	public abstract String toString();

	@Override
	public int compareTo(@NotNull StoredIdentifier storedIdentifier) {
		return Long.compare(dateMillis, storedIdentifier.dateMillis);
	}
}
