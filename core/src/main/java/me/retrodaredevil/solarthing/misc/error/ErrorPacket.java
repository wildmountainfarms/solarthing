package me.retrodaredevil.solarthing.misc.error;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(ExceptionErrorPacket.class)
})
public interface ErrorPacket extends TypedDocumentedPacket<ErrorPacketType> {
}
