package me.retrodaredevil.solarthing.type.closed.meta;

import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TargetedMetaPacket extends MetaPacket, TypedDocumentedPacket<TargetedMetaPacketType> {
}
