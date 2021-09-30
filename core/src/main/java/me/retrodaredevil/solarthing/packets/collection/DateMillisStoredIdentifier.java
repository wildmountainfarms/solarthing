package me.retrodaredevil.solarthing.packets.collection;


import java.util.Objects;

/**
 * Note: This should only be used in tests or for a binary search
 */
public class DateMillisStoredIdentifier extends StoredIdentifier {

	public DateMillisStoredIdentifier(long dateMillis) {
		super(dateMillis);
	}

	@Override
	public boolean equals(Object o) {
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
