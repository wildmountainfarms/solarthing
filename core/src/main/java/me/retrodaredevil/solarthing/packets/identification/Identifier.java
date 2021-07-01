package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverIdentifier;
import me.retrodaredevil.solarthing.solar.tracer.TracerIdentifier;

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
 * <p>
 * Some Identifiers can be serialized to JSON and deserialized from JSON. However, serialization and deserialization should not be considered stable.
 *
 */
@JsonSubTypes({
		@JsonSubTypes.Type(DataIdentifier.class),
		@JsonSubTypes.Type(OutbackIdentifier.class),
		@JsonSubTypes.Type(RoverIdentifier.class),
		@JsonSubTypes.Type(TracerIdentifier.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type") // don't use "EXISTING_PROPERTY" because we want it to serialize the name we give the subclass without overhead of a getter
@JsonClassDescription("Contains a representation that is unique across all packets in a particular fragment")
public interface Identifier {
	/**
	 * Also note that representations could change at any time after a SolarThing update, but generally they don't.
	 * @return A representation of this identifier
	 */
	@GraphQLInclude("representation")
	@JsonPropertyDescription("A string representation of this identifier")
	@NotNull String getRepresentation();

}
