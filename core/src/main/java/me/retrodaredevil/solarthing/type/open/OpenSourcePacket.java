package me.retrodaredevil.solarthing.type.open;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

/**
 * By implementing this interface and by adding your class to the {@link JsonSubTypes}, you enable a given packet to be serialized and deserialized
 * in {@link OpenSource#getPacket()}
 * <p>
 * This class should not be referenced in most places. This is not a shortcut to use when registering your subtypes. This is only here because of technical
 * limitations of not being able to define which classes to be able to deserialize in OpenSource.
 * <p>
 * NOTE: Implementations of this interface should implement {@link #equals(Object)}
 */
@JsonSubTypes(
		@JsonSubTypes.Type(CommandOpenPacket.class)
)
public interface OpenSourcePacket extends DocumentedPacket, UniqueStringRepresentation {
}
