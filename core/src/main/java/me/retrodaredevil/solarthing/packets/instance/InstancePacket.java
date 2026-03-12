package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(InstanceSourcePacket.class),
		@JsonSubTypes.Type(InstanceFragmentIndicatorPacket.class),
		@JsonSubTypes.Type(InstanceTargetPacket.class)
})
@NullMarked
public interface InstancePacket extends TypedDocumentedPacket<InstancePacketType> {
}
