package me.retrodaredevil.solarthing.misc.error;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(ExceptionErrorPacket.class)
})
@NullMarked
public interface ErrorPacket extends TypedDocumentedPacket<ErrorPacketType> {
}
