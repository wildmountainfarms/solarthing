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

	/**
	 *
	 * @param object The object/value. May be null, or may not be. You decide.
	 * @param frequency The frequency or null, to indicate default
	 */
	@JsonCreator // Jackson 2.19 supports JsonUnwrapped within JsonCreator https://github.com/FasterXML/jackson-databind/issues/1467
	public FrequentObject(@JsonUnwrapped T object, @JsonProperty("frequency") Integer frequency) {
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
		return Objects.equals(object, that.object) && Objects.equals(frequency, that.frequency);
	}

	@Override
	public int hashCode() {
		return Objects.hash(object, frequency);
	}
}
