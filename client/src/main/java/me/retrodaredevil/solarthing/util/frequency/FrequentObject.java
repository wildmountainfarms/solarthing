package me.retrodaredevil.solarthing.util.frequency;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.util.Objects;

@JsonExplicit
public final class FrequentObject<T> {
	@JsonUnwrapped
	private final T object;

	@JsonProperty("frequency")
	private final Integer frequency;

	private FrequentObject(){
		// Constructor that Jackson calls
		object = null;
		frequency = null;
	}
	/**
	 *
	 * @param object The object/value. May be null, or may not be. You decide.
	 * @param frequency The frequency or null, to indicate default
	 */
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
