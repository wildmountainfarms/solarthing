package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import me.retrodaredevil.solarthing.annotations.NotNull;

/**
 * All {@link Identifier}s have their {@link #equals(Object)} and {@link #hashCode()} methods implemented so
 * they can be used as unique keys in a single fragment.
 * <p>
 * Identifiers are unique across a single fragment, so if you compare across fragments, there may be identifiers that are equal to identifiers in other fragments.
 * To solve this, you can use {@link IdentifierFragment}
 * <p>
 * {@link Identifier}s also inherit {@link Comparable<Identifier>} so they can be sorted using the default SolarThing order. (Their natural order).
 * <p>
 * The string representation should not contain slashes (/) and should generally avoid special Non alpha-numeric characters.
 * The exception to this are '(', ')', '='.
 */
@JsonClassDescription("Contains a representation that is unique across all packets in a particular fragment")
public interface Identifier {
	/**
	 * @return A representation of this identifier
	 */
	@JsonProperty("representation")
	@JsonPropertyDescription("A string representation of this identifier")
	@NotNull String getRepresentation();

}
