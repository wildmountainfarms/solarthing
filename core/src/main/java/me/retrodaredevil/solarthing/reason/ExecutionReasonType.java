package me.retrodaredevil.solarthing.reason;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ExecutionReasonType implements DocumentedPacketType {
	SOURCE,
	PACKET_COLLECTION
}
