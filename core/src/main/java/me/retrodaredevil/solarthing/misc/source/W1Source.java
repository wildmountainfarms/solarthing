package me.retrodaredevil.solarthing.misc.source;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonExplicit
@JsonTypeName("w1")
public final class W1Source implements DeviceSource {
	private final String name;

	@JsonCreator
	public W1Source(
			@JsonProperty(value = "name", required = true) String name
	) {
		requireNonNull(this.name = name);
	}

	@JsonProperty("name")
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "W1Source(" +
				"name='" + name + '\'' +
				')';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		W1Source w1Source = (W1Source) o;
		return getName().equals(w1Source.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}
}
