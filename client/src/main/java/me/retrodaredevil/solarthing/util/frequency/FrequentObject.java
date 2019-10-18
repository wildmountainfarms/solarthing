package me.retrodaredevil.solarthing.util.frequency;

import java.util.Objects;

public final class FrequentObject<T> {
	private final T object;


	private final Integer frequency;

	public FrequentObject(T object, Integer frequency) {
		this.object = object;
		this.frequency = frequency;
	}

	public T getObject() {
		return object;
	}

	public Integer getFrequency() {
		return frequency;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FrequentObject<?> that = (FrequentObject<?>) o;
		return Objects.equals(object, that.object) &&
			Objects.equals(frequency, that.frequency);
	}

	@Override
	public int hashCode() {
		return Objects.hash(object, frequency);
	}
}
