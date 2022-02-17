package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

/**
 * Represents a packet relating to security or encrypting for integrity.
 * <p>
 * These types of packets should be stored in a packet collection with limited packets. That means a packet collection
 * containing these packets should have as few packets as possible. The only other packets that should be alongside
 * this are SOURCE and TARGET packets.
 */
@JsonSubTypes({
		@JsonSubTypes.Type(IntegrityPacket.class),
		@JsonSubTypes.Type(LargeIntegrityPacket.class),
		@JsonSubTypes.Type(AuthNewSenderPacket.class),
})
public interface SecurityPacket extends TypedDocumentedPacket<SecurityPacketType> {
}
