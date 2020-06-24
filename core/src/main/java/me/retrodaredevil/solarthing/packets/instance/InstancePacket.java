package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;

@JsonSubTypes({
		@JsonSubTypes.Type(InstanceSourcePacket.class),
		@JsonSubTypes.Type(InstanceFragmentIndicatorPacket.class),
		@JsonSubTypes.Type(InstanceTargetPacket.class)
})
public interface InstancePacket extends TypedDocumentedPacket<InstancePacketType> {
}
