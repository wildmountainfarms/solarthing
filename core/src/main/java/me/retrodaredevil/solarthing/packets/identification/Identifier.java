package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * All {@link Identifier}s have their {@link #equals(Object)} and {@link #hashCode()} methods implemented so
 * they can be used as unique keys in a single fragment.
 * <p>
 * Most {@link Identifier}s also inherit {@link Comparable<Identifier>} so they can be sorted using the default SolarThing order.
 */
public interface Identifier {
	/**
	 * @return A representation of this identifier
	 */
	@JsonProperty("representation")
	String getRepresentation();
}
