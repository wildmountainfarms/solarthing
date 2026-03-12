package me.retrodaredevil.solarthing.packets.collection;


import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Note: This should only be used in tests or for a binary search
 */
@NullMarked
public class DateMillisStoredIdentifier extends StoredIdentifier {

	public DateMillisStoredIdentifier(long dateMillis) {
		super(dateMillis);
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DateMillisStoredIdentifier that = (DateMillisStoredIdentifier) o;
		return dateMillis == that.dateMillis;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateMillis);
	}

	@Override
	public String toString() {
		return "DateMillisStoredIdentifier(" +
				"dateMillis=" + dateMillis +
				')';
	}
}
