package me.retrodaredevil.solarthing.type.event.feedback;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum FeedbackPacketType implements DocumentedPacketType {
	EXECUTION_FEEDBACK,
	HEARTBEAT,
}
