package me.retrodaredevil.solarthing.reason;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

/**
 * Represents the reason for the execution/event/cause of something.
 * <p>
 * NOTE: This extends {@link TypedDocumentedPacket} because using it makes the handling of subtypes easier and more consistent.
 * <p>
 * NOTE: Implementations of this interface should implement {@link #equals(Object)}
 */
@JsonSubTypes({
		@JsonSubTypes.Type(OpenSourceExecutionReason.class)
})
@JsonExplicit
public interface ExecutionReason extends TypedDocumentedPacket<ExecutionReasonType>, UniqueStringRepresentation {
}
