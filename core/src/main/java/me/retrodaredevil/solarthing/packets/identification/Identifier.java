package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * All {@link Identifier}s have their {@link #equals(Object)} and {@link #hashCode()} methods implemented so
 * they can be used as unique keys in a single fragment.
 * <p>
 * Identifiers are unique across a single fragment, so if you compare across fragments, there may be identifiers that are equal to identifiers in other fragments.
 * To solve this, you can use {@link IdentifierFragment}
 * <p>
 * {@link Identifier}s also inherit {@link Comparable<Identifier>} so they can be sorted using the default SolarThing order. (Their natural order).
 */
public interface Identifier extends Comparable<Identifier> {
	/**
	 * @return A representation of this identifier
	 */
	@JsonProperty("representation")
	String getRepresentation();

	@Override
	int compareTo(@NotNull Identifier identifier);
}
