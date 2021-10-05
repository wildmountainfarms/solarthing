package me.retrodaredevil.solarthing.type.alter.flag;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

import java.time.Instant;

@JsonSubTypes({
		@JsonSubTypes.Type(TimeRangeActivePeriod.class),
})
public interface ActivePeriod extends TypedDocumentedPacket<ActivePeriodType>, UniqueStringRepresentation {

	boolean isActive(long dateMillis);
	boolean isActive(Instant instant);
}
